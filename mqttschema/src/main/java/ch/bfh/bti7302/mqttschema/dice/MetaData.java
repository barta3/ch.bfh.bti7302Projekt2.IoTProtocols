package ch.bfh.bti7302.mqttschema.dice;

import java.util.List;

import ch.bfh.bti7302.mqttschema.Data;

public class MetaData {
    private Data data;
    private DiceConfig status;
    private List<Request> reguests;

    public DiceConfig getStatus() {
        return status;
    }

    public void setStatus(DiceConfig status) {
        this.status = status;
    }

    public List<Request> getReguests() {
        return reguests;
    }

    public void setReguests(List<Request> reguests) {
        this.reguests = reguests;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

}
