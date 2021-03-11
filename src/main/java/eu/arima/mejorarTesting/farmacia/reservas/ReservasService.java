package eu.arima.mejorarTesting.farmacia.reservas;

import eu.arima.mejorarTesting.farmacia.medicamentos.Medicamento;
import eu.arima.mejorarTesting.farmacia.medicamentos.MedicamentosRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReservasService {

    public static final double FACTOR_PEDIDO_ALMACEN = 1.5;
    private final ReservasRepository reservasRepository;
    private final MedicamentosRepository medicamentosRepository;
    private final PedidosAlmacenService pedidosAlmacenService;

    public ReservasService(ReservasRepository reservasRepository,
                           MedicamentosRepository medicamentosRepository, PedidosAlmacenService pedidosAlmacenService) {
        this.reservasRepository = reservasRepository;
        this.medicamentosRepository = medicamentosRepository;
        this.pedidosAlmacenService = pedidosAlmacenService;
    }


    @Transactional
    public InfoRecogidaReserva reservarMedicamento(long idMedicamento, int unidades) {
        Medicamento medicamento = medicamentosRepository.findById(idMedicamento).orElseThrow();
        int unidadesReserva = unidades;
        int unidadesPendientes = 0;
        if (!medicamento.tieneStockSuficiente(unidades)) {
            unidadesReserva = medicamento.getUnidadesStock();
            unidadesPendientes = unidades - unidadesReserva;
            pedidosAlmacenService.realizarPedido(idMedicamento, (int) (unidadesPendientes * FACTOR_PEDIDO_ALMACEN));
        }
        InfoRecogidaReserva infoRecogidaReserva = null;
        if (unidadesReserva > 0) {
            Reserva reserva = reservasRepository.save(new Reserva(idMedicamento, unidadesReserva));
            medicamento.disminuirStock(unidadesReserva);
            medicamentosRepository.save(medicamento);
            infoRecogidaReserva = new InfoRecogidaReserva(reserva.getId(), unidadesReserva, unidadesPendientes);
        }
        return infoRecogidaReserva != null ? infoRecogidaReserva : new InfoRecogidaReserva(unidadesReserva, unidadesPendientes);
    }

    public List<Reserva> getAllReservas() {
        return reservasRepository.findAll();
    }
}
