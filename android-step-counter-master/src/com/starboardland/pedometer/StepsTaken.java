/**
 * StepsTaken.java
 * Sam Fitness
 *
 * @version 1.0.0
 *
 * @author Jake Haas
 * @author Evan Safford
 * @author Nate Ford
 * @author Haley Andrews
 *
 * Copyright (c) 2014, 2015. Wellness-App-MQP. All Rights Reserved.
 *
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY 
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */

package com.starboardland.pedometer;

public class StepsTaken {
    private int id;
    int steps;

    public StepsTaken() {
    }

    public StepsTaken(int steps) {
        super();
        this.steps = steps;
    }

    // getters & setters

    @Override
    public String toString() {
        return "StepsTaken [id=" + id + ", steps=" + steps + "]";
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    // Will be used by the ArrayAdapter in the ListView
}
