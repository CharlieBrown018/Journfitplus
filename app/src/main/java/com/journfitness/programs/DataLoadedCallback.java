package com.journfitness.programs;

import com.journfitness.DAO.program.Program;

import java.util.List;

public interface DataLoadedCallback {
    void onDataLoaded(List<Program> programs);
}
