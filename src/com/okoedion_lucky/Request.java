package com.okoedion_lucky;

public class Request {
    int firstBank;
    int secondBank;
    double thresholdProbability;

    public Request(int firstBank, int secondBank, double thresholdProbability) {
        this.firstBank = firstBank;
        this.secondBank = secondBank;
        this.thresholdProbability = thresholdProbability;
    }
}
