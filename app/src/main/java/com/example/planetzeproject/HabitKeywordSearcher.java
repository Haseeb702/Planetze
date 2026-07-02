package com.example.planetzeproject;

import java.util.ArrayList;
import java.util.List;

public class HabitKeywordSearcher extends HabitSearcher{
    private String target = "";
    public HabitKeywordSearcher(String target) {
        this.target = target;
    }
    public List<String> search() {
        List<String> matchingHabits = new ArrayList<String>();
        for (String habit : habits) {
            if (habit.contains(target)) {
                matchingHabits.add(habit);
            }
        }
        return matchingHabits;
    }


}
