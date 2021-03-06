package eu.arima.mejorarTesting.farmacia.medicamentos;

import org.springframework.stereotype.Service;

@Service
public class MedicamentosService {

    private final MedicamentosRepository medicamentosRepository;

    public MedicamentosService(MedicamentosRepository medicamentosRepository) {
        this.medicamentosRepository = medicamentosRepository;
    }

    public Medicamento getMedicamento(long idMedicamento) {
        Medicamento medicamento = medicamentosRepository.findById(idMedicamento).orElseThrow();
        if (medicamento.estaCaducado()) {
            throw new MedicamentoCaducadoException();
        }
        return medicamento;
    }

    public void actualizarStock(long idMedicamento, int unidades) {
        Medicamento medicamento = medicamentosRepository.findById(idMedicamento).orElseThrow();
        medicamento.setUnidadesStock(unidades);
        medicamentosRepository.save(medicamento);
    }
}
