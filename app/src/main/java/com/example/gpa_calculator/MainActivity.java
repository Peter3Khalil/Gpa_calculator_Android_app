package com.example.gpa_calculator;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView arrowBack;
    private int globalSemesterCredits=0;
    TextView semsterGpaTextView,semsterCreditsTextView,cumulativeGpaTextView,cumulativeCreditsTextView,gpaOfNextSemsterTextView,hintTextView;
    private EditText prevGpaEditText,totalCreditsEditText,editTextOfTableRowChild,currentGpaEditText,
           targetGpaEditText,currentCreditsEditText,additionalCreditsEditText ;
    private Button calculateBtn,insertRowBtn,deleteRowBtn,clearBtn,calculateTargetGpaBtn;
    private TableLayout table;
    private TableRow tableRow;
    LinearLayout gpaCalculatorContainer,targetGpaCalculatorContainer;
    Switch switchBtn;
    private static int i = 2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gpaCalculatorContainer = findViewById(R.id.gpa_calculator);
        targetGpaCalculatorContainer = findViewById(R.id.target_gpa_calculator);
        currentGpaEditText = findViewById(R.id.current_gpa);
        targetGpaEditText = findViewById(R.id.target_gpa);
        currentCreditsEditText = findViewById(R.id.current_credits);
        additionalCreditsEditText = findViewById(R.id.additional_credits);
        table = findViewById(R.id.table);

//        Handle Back Press
        arrowBack = findViewById(R.id.arrow_back);
        arrowBack.setOnClickListener(view -> onBackPressed());
//        End of Handle Back Press

//        Handle Input Type
        prevGpaEditText = findViewById(R.id.previous_gpa);
        totalCreditsEditText = findViewById(R.id.total_credits);

        prevGpaEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        totalCreditsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        //Grade Cell
        tableRow = (TableRow) table.getChildAt(1);
        editTextOfTableRowChild = (EditText) tableRow.getChildAt(1);
        editTextOfTableRowChild.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //Credit Cell
        editTextOfTableRowChild = (EditText) tableRow.getChildAt(2);
        editTextOfTableRowChild.setInputType(InputType.TYPE_CLASS_NUMBER);

        currentGpaEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        targetGpaEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

        currentCreditsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        additionalCreditsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//        End of Handle Input Type

//      Setting Background of buttons
        calculateBtn = findViewById(R.id.calculate_btn);
        calculateBtn.setBackgroundColor(Color.BLACK);

        insertRowBtn = findViewById(R.id.insert_row_btn);
        insertRowBtn.setBackgroundColor(Color.BLACK);

        deleteRowBtn = findViewById(R.id.delete_row_btn);
        deleteRowBtn.setBackgroundColor(Color.GRAY);

        clearBtn = findViewById(R.id.clear_btn);
        clearBtn.setBackgroundColor(Color.DKGRAY);

        calculateTargetGpaBtn = findViewById(R.id.calculate_target_gpa_btn);
        calculateTargetGpaBtn.setBackgroundColor(Color.BLACK);
//      End of setting

//        handle Insert Row Button
        insertRowBtn.setOnClickListener(view -> insertRow());
//        End of Handle Insert Row Button

//        Handle Delete Button
        deleteRowBtn.setOnClickListener(view -> {
            deleteRow();
            calculateGpa();
        });
//        End of Handle Delete Button

//        Handle Calculate Button
        calculateBtn = findViewById(R.id.calculate_btn);
        calculateBtn.setOnClickListener(view -> calculateGpa());
//        End of Handle Calculate Button

//        Handle Clear Button
        clearBtn.setOnClickListener(view -> clear());
//        End of Handle Clear Button

//        Handle Switch Button
        switchBtn = findViewById(R.id.switch_to_target_gpa);
        switchBtn.setOnCheckedChangeListener((compoundButton, b) -> {
        if(b){
            targetGpaCalculatorContainer.setVisibility(View.VISIBLE);
            gpaCalculatorContainer.setVisibility(View.GONE);
        }else {
            targetGpaCalculatorContainer.setVisibility(View.GONE);
            gpaCalculatorContainer.setVisibility(View.VISIBLE);        }
        });
//        End of Handle Switch Button

//        Handle Calculate Target Gpa Button
        calculateTargetGpaBtn.setOnClickListener(view -> calculateOfTargetGpa());
//        End of Handle Calculate Target Gpa Button
    }
    public void insertRow(){
        if(i<table.getChildCount()){
        tableRow = (TableRow) table.getChildAt(i);
//        Handle Input Type
        //Grade Cell
        editTextOfTableRowChild = (EditText) tableRow.getChildAt(1);
        editTextOfTableRowChild.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        InputFilter[] filter = new InputFilter[1];
        filter[0] = new InputFilter.LengthFilter(4);
        editTextOfTableRowChild.setFilters(filter);
        //Credit Cell
        editTextOfTableRowChild = (EditText) tableRow.getChildAt(2);
        editTextOfTableRowChild.setInputType(InputType.TYPE_CLASS_NUMBER);
        filter[0] = new InputFilter.LengthFilter(1);
        editTextOfTableRowChild.setFilters(filter);
//        End of Handle Input Type

        tableRow.setVisibility(View.VISIBLE);
        i++;
        }
    }

    public void deleteRow(){
        if(i>2){
        tableRow = (TableRow) table.getChildAt(i-1);
        editTextOfTableRowChild = (EditText) tableRow.getChildAt(1);
        editTextOfTableRowChild.setText("");

        editTextOfTableRowChild = (EditText) tableRow.getChildAt(2);
        editTextOfTableRowChild.setText("");
        tableRow.setVisibility(View.GONE);
        i--;
        }
    }

    public Double getSemesterGpa(){
        String grade,credit;
        double gradeValue=0.0,gradeProductCredit=0,semsterGpa=0.0,Cgpa;
        int creditValue=0;
        globalSemesterCredits=0;
        for (int j = 1; j <table.getChildCount() ; j++) {
            tableRow = (TableRow) table.getChildAt(j);
            editTextOfTableRowChild = (EditText) tableRow.getChildAt(1);
            grade = editTextOfTableRowChild.getText().toString().trim();

            editTextOfTableRowChild = (EditText) tableRow.getChildAt(2);
            credit = editTextOfTableRowChild.getText().toString().trim();
            if(!grade.isEmpty() && !credit.isEmpty()){
                //Validate Inputs
                try {
                    gradeValue = Double.parseDouble(grade);
                    creditValue = Integer.parseInt(credit);
                    globalSemesterCredits += creditValue;
                    gradeProductCredit += gradeValue*creditValue;
                }catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(),"Error:Inputs should be number",Toast.LENGTH_LONG).show();
                }
            }
        }
        semsterGpa = gradeProductCredit/globalSemesterCredits;
        semsterGpaTextView = findViewById(R.id.semster_gpa);
        semsterGpaTextView.setText(String.valueOf(semsterGpa));
        semsterCreditsTextView = findViewById(R.id.semster_credits_hours);
        semsterCreditsTextView.setText(String.valueOf(globalSemesterCredits));
        return semsterGpa;
    }
    public Double getCumulativeGpa(){
        double prevGpaValue=0.0,cgpa=0.0;
        int prevTotalCreditsHoursValue=0;
        String prevGpa = prevGpaEditText.getText().toString().trim();
        String prevTotalCreditsHours = totalCreditsEditText.getText().toString().trim();
        cumulativeGpaTextView = findViewById(R.id.cumulative_gpa);
        cumulativeCreditsTextView = findViewById(R.id.cumulative_credits_hours);
        if(!prevGpa.isEmpty() && !prevTotalCreditsHours.isEmpty()){
            //Validate inputs
            try {
                prevGpaValue = Double.parseDouble(prevGpa);
                prevTotalCreditsHoursValue = Integer.parseInt(prevTotalCreditsHours);
                int sumOfAllCreditsHours = prevTotalCreditsHoursValue+globalSemesterCredits;
                cgpa = (getSemesterGpa()*globalSemesterCredits + prevGpaValue*prevTotalCreditsHoursValue) /(sumOfAllCreditsHours);
                cumulativeGpaTextView.setText(String.valueOf(cgpa));
                cumulativeCreditsTextView.setText(String.valueOf(sumOfAllCreditsHours));
            }catch (NumberFormatException e){
                Toast.makeText(getApplicationContext(),"Error:Inputs should be number",Toast.LENGTH_LONG).show();
            }

        }
        else{
            cumulativeGpaTextView.setText(String.valueOf(getSemesterGpa()));
            cumulativeCreditsTextView.setText(String.valueOf(globalSemesterCredits));
        }
        return cgpa;
    }
    public void calculateGpa(){
        getSemesterGpa();
        getCumulativeGpa();
    }
    public void clear(){
        //Clear all Text Views
        semsterGpaTextView = findViewById(R.id.semster_gpa);
        semsterCreditsTextView = findViewById(R.id.semster_credits_hours);
        cumulativeGpaTextView = findViewById(R.id.cumulative_gpa);
        cumulativeCreditsTextView = findViewById(R.id.cumulative_credits_hours);

        semsterGpaTextView.setText("0.0");
        semsterCreditsTextView.setText("0");
        cumulativeGpaTextView.setText("0.0");
        cumulativeCreditsTextView.setText("0");

        //Clear Edit Texts(prevGpa,prevTotalCredits)
        prevGpaEditText.setText("");
        totalCreditsEditText.setText("");

        //Clear Table Values
        for (int j = 1; j < table.getChildCount(); j++) {
            tableRow = (TableRow) table.getChildAt(j);
            editTextOfTableRowChild = (EditText) tableRow.getChildAt(1);
            editTextOfTableRowChild.setText("");
            editTextOfTableRowChild = (EditText) tableRow.getChildAt(2);
            editTextOfTableRowChild.setText("");
        }
    }

    public void calculateOfTargetGpa(){
        gpaOfNextSemsterTextView = findViewById(R.id.gpa_of_next_semster_text_view);
        hintTextView = findViewById(R.id.hint);

        double currentGpaValue=0.0,targetGpaValue=0.0,semsterGpa=0.0;
        int currentCreditsValue=0,additionalCreditsValue=0;
        String currentGpa = currentGpaEditText.getText().toString().trim();
        String targetGpa= targetGpaEditText.getText().toString().trim();
        String currentCredits = currentCreditsEditText.getText().toString().trim();
        String additionalCredits = additionalCreditsEditText.getText().toString().trim();

        if(!currentGpa.isEmpty() && !targetGpa.isEmpty() && !currentCredits.isEmpty() && !additionalCredits.isEmpty()){
            //Validate Inputs
            try {
                currentGpaValue = Double.parseDouble(currentGpa);
                targetGpaValue = Double.parseDouble(targetGpa);
                currentCreditsValue = Integer.parseInt(currentCredits);
                additionalCreditsValue = Integer.parseInt(additionalCredits);
                //targetGpa = (semsterGpa*additionalCredits+currentGpa*currentCredits) / currentCredits+additionalCredits
                //targetGpa*(currentCredits+additionalCredits) = (semsterGpa*additionalCredits+currentGpa*currentCredits)
                //targetGpa*(currentCredits+additionalCredits) - currentGpa*currentCredits = semsterGpa*additionalCredits
                //(targetGpa*(currentCredits+additionalCredits) - currentGpa*currentCredits)/additionalCredits = semsterGpa
                //so semsterGpa = (targetGpa*(currentCredits+additionalCredits) - currentGpa*currentCredits)/additionalCredits
                semsterGpa = (targetGpaValue*(additionalCreditsValue+currentCreditsValue)-currentGpaValue*currentCreditsValue)/additionalCreditsValue;

                String temp = String.valueOf(semsterGpa);
                if(temp.length()>4)
                    temp = temp.substring(0,4);
                gpaOfNextSemsterTextView.setText("GPA of next semster should be = "+temp);

                if(semsterGpa>4 && targetGpaValue<4){
                    double creditsShouldRegister=0.0;
                    creditsShouldRegister = ((targetGpaValue*currentCreditsValue-currentGpaValue*currentCreditsValue)/(4-targetGpaValue));
                    hintTextView.setVisibility(View.VISIBLE);
                    hintTextView.setText("Hint:This is not possible,To achieve 4 GPA ,you should register "+(int)Math.floor(creditsShouldRegister)+" Credits Hours");
                }else{
                    hintTextView.setVisibility(View.GONE);
                }

            }catch (NumberFormatException e){
                Toast.makeText(getApplicationContext(),"Error:Inputs should be number",Toast.LENGTH_LONG).show();
            }
        }

        else{
            Toast.makeText(getApplicationContext(),"Please Fill all inputs fields",Toast.LENGTH_SHORT).show();
        }
    }
}