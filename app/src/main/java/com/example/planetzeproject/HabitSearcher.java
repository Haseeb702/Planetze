package com.example.planetzeproject;

import java.util.List;

public abstract class HabitSearcher {
    protected List<String> habits;
    //Returns an ArrayList<String> of habits that match the criterion set by the subclass
    public abstract List<String> search();
}
