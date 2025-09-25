package dan.competition.device.model;

import java.util.List;

public class BatchMessage {
    private int batchIndex;
    private int totalBatches;
    private List<MedicalDataDTO> items;

    public BatchMessage(int batchIndex, int totalBatches, List<MedicalDataDTO> items) {
        this.batchIndex = batchIndex;
        this.totalBatches = totalBatches;
        this.items = items;
    }

    public int getBatchIndex() { return batchIndex; }
    public int getTotalBatches() { return totalBatches; }
    public List<MedicalDataDTO> getItems() { return items; }
}
