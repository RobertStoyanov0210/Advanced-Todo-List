package com.f90782.advancedtodolist.adapter;

import com.f90782.advancedtodolist.model.Task;

public interface OnTodoClickListener {
    void onTodoClick(Task task);
    void onTodoRadioButtonClick(Task task);
}
