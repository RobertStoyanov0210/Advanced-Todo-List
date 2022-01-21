package com.f90782.advancedtodolist;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

import com.f90782.advancedtodolist.model.Priority;
import com.f90782.advancedtodolist.model.SharedViewModel;
import com.f90782.advancedtodolist.model.Task;
import com.f90782.advancedtodolist.model.TaskViewModel;
import com.f90782.advancedtodolist.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment implements View.OnClickListener {
    Calendar calendar = Calendar.getInstance();
    private EditText enterTodo;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    private boolean isEdit;
    private Priority priority;
    private SharedViewModel sharedViewModel;

    public BottomSheetFragment() {

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);

        Chip todayChip = view.findViewById(R.id.today_chip);
        todayChip.setOnClickListener(this);
        Chip tommorowChip = view.findViewById(R.id.tomorrow_chip);
        tommorowChip.setOnClickListener(this);
        Chip nextWeekChip = view.findViewById(R.id.next_week_chip);
        nextWeekChip.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //        If item is selected
        if (sharedViewModel.getSelectedItem().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();
//            show item's name on the editable fields
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
            Log.d("MY", "onViewCreated: " + task.getTask());
        }
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

//      CALENDAR BUTTON
        calendarButton.setOnClickListener(view12 -> {
            Utils.hideKeyboard(view12);
//           On click -> change the view to either gone or visigle
            calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ?
                    View.VISIBLE : View.GONE);

        });
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMoth) -> {
            calendar.clear();
            calendar.set(year, month, dayOfMoth);
            dueDate = calendar.getTime();
        });

//        PRIORITY BUTTON
        priorityButton.setOnClickListener(view13 -> {
            Utils.hideKeyboard(view13);
//           On click -> change the view to either gone or visigle
            priorityRadioGroup.setVisibility(priorityRadioGroup.getVisibility() == View.GONE ?
                    View.VISIBLE : View.GONE);
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                if (priorityRadioGroup.getVisibility() == View.VISIBLE) {
                    selectedButtonId = checkedId;
                    selectedRadioButton = view.findViewById(selectedButtonId);
                    if (selectedRadioButton.getId() == R.id.radioButton_high) {
                        priority = Priority.HIGH;
                    } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                        priority = Priority.MEDIUM;
                    } else
                        priority = Priority.LOW;
                }else{
                    priority = Priority.LOW;
                }
            });

        });

//        SAVE BUTTON
        saveButton.setOnClickListener(view1 -> {
            String task = enterTodo.getText().toString().trim();
            if (!TextUtils.isEmpty(task) && dueDate != null && priority != null) {
                Task myTask = new Task(task, priority,
                        dueDate, Calendar.getInstance().getTime(),
                        false);
                if (isEdit) {
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();

                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueDate);
                    TaskViewModel.update(updateTask);

                    sharedViewModel.setIsEdit(false);
                } else
                    TaskViewModel.insert(myTask);

                enterTodo.setText("");
//                On Save bottomsheet -> hidden
                if (this.isVisible()) {
                    this.dismiss();
                }

            } else {
                Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
//        Attach chips for today, tomorrow and next week on the task
        int id = view.getId();
        if (id == R.id.today_chip) {
            //set data for today
            calendar.add(Calendar.DAY_OF_YEAR, 0);
            dueDate = calendar.getTime();
            Log.d("TIME", "onClick: " + dueDate.toString());


        } else if (id == R.id.tomorrow_chip) {

            calendar.add(Calendar.DAY_OF_YEAR, 1);
            dueDate = calendar.getTime();
            Log.d("TIME", "onClick: " + dueDate.toString());

        } else if (id == R.id.next_week_chip) {

            calendar.add(Calendar.DAY_OF_YEAR, 7);
            dueDate = calendar.getTime();
            Log.d("TIME", "onClick: " + dueDate.toString());

        }

    }
}