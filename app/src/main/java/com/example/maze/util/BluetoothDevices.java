package com.example.maze.util;

public class BluetoothDevices {


    public String nomeDispositivo;
    public String indirizzoMac;

    public BluetoothDevices(String nomeDispositivo, String indirizzoMac) {
        this.nomeDispositivo = nomeDispositivo;
        this.indirizzoMac = indirizzoMac;
    }

    public String getNomeDispositivo() {
        return nomeDispositivo;
    }

    public String getIndirizzoMac() {
        return indirizzoMac;
    }

}
