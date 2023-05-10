package com.example.vktest;

import java.util.Objects;

public class DataFile {
    public String nameFile, sizeFile, dateFile;

    public DataFile(String nameFile, String sizeFile, String dateFile) {
        this.nameFile = nameFile;
        this.sizeFile = sizeFile;
        this.dateFile = dateFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public String getSizeFile() {
        return sizeFile;
    }

    public String getDateFile() {
        return dateFile;
    }

    public DataFile() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataFile dataFile = (DataFile) o;
        return Objects.equals(nameFile, dataFile.nameFile) && Objects.equals(sizeFile, dataFile.sizeFile) && Objects.equals(dateFile, dataFile.dateFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameFile, sizeFile, dateFile);
    }
}
