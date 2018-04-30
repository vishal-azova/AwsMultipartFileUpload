package com.example.php_dev4.restapi;

public class PartRead {

    int partNumber;
    int startReadIndex;
    int endReadIndex;

    public int getStartReadIndex() {
        return startReadIndex;
    }

    public void setStartReadIndex(int startReadIndex) {
        this.startReadIndex = startReadIndex;
    }

    public int getEndReadIndex() {
        return endReadIndex;
    }

    public void setEndReadIndex(int endReadIndex) {
        this.endReadIndex = endReadIndex;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }
}
