package com.example.gpa_calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final ArrayList<View> arrayOfViews = new ArrayList<>(10);
    private final String errorMessage = "GPA should be smaller than or equal 4";
    private final String[] gradesArray = {"", "A", "-A", "B+", "B", "C+", "C", "D", "F"};
    private final String[] creditsArray = {"1", "2", "3", "4", "5", "6", "7"};
    private ArrayAdapter<String> adapter, adapter2;
    private EditText prevGpaEditText, totalCreditsEditText;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_style, gradesArray);
        adapter2 = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_style, creditsArray);
        adapter.setDropDownViewResource(R.layout.spinner_style);//Set adapter
        arrayOfViews.add(findViewById(R.id.display_linear_layout));// 0
        arrayOfViews.add(findViewById(R.id.gpa_and_credits_linear_layout));// 1
        arrayOfViews.add(findViewById(R.id.table));// 2
        arrayOfViews.add(findViewById(R.id.btns_linear_layout));// 3
        arrayOfViews.add(findViewById(R.id.main_layout));// 4
        addRow();//Add first Row at the beginning
        //Handle Edits Text
        LinearLayout parentOfEditTexts = (LinearLayout) arrayOfViews.get(1);
        final double[] prevGpaValue = {0.0};
        prevGpaEditText = (EditText) parentOfEditTexts.getChildAt(0);//Previous Gpa
        totalCreditsEditText = (EditText) parentOfEditTexts.getChildAt(2);//Total Credits
        totalCreditsEditText.setHint("Total Credits Hours");
        TextView error = (TextView) parentOfEditTexts.getChildAt(1);
        prevGpaEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);//Float or Integer
        totalCreditsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);//Integer
        //Validate Inputs
        prevGpaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    calculate();
                    try {
                        prevGpaValue[0] = Double.parseDouble(charSequence.toString());
                        if (prevGpaValue[0] > 4) {
                            error.setText(errorMessage);
                            error.setVisibility(View.VISIBLE);
                        } else {
                            error.setVisibility(View.GONE);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Invalid Inputs : from edit text", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    error.setVisibility(View.GONE);
                    calculate();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        totalCreditsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //End of Validate Inputs
        //End of Handle Edit Text

        //Handle Add Row Icon
        LinearLayout parentOfBtns = (LinearLayout) arrayOfViews.get(3);
        LinearLayout parentOfAddIcon = (LinearLayout) parentOfBtns.getChildAt(0);
        ImageView addIcon = (ImageView) parentOfAddIcon.getChildAt(0);
        addIcon.setOnClickListener(view -> addRow());
        //End of Handle Add Row Icon

        //Handle Switch  Button
        switchBtn = findViewById(R.id.switch_btn);
        LinearLayout gpaCalculator = findViewById(R.id.gpa_calculator);
        LinearLayout targetGpa = findViewById(R.id.target_gpa_calculator);
        switchBtn.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                gpaCalculator.setVisibility(View.GONE);
                targetGpa.setVisibility(View.VISIBLE);
            } else {
                gpaCalculator.setVisibility(View.VISIBLE);
                targetGpa.setVisibility(View.GONE);
            }
        });
        //End of Handle Switch Button

        //Handle Target Gpa Calculator
        EditText editText = findViewById(R.id.current_gpa);//Current Gpa
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView errorMsg = findViewById(R.id.error_current_gpa);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateGpaOfNextSemester();
                calculateCGPAIfSemesterGpaEqual();
                if (!charSequence.toString().isEmpty()) {
                    if (getCurrentGpa() > 4) {
                        errorMsg.setVisibility(View.VISIBLE);
                        errorMsg.setText(errorMessage);
                    } else {
                        errorMsg.setVisibility(View.GONE);
                    }
                } else {
                    errorMsg.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText = findViewById(R.id.target_gpa);//Target Gpa
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextView errorMsg2 = findViewById(R.id.error_target_gpa);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateGpaOfNextSemester();
                if (!charSequence.toString().isEmpty()) {
                    if (getTargetGpa() > 4) {
                        errorMsg2.setVisibility(View.VISIBLE);
                        errorMsg2.setText(errorMessage);
                    } else {
                        errorMsg2.setVisibility(View.GONE);
                    }
                } else {
                    errorMsg2.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        editText = findViewById(R.id.current_credits);//Current Credits
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateGpaOfNextSemester();
                calculateCGPAIfSemesterGpaEqual();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editText = findViewById(R.id.additional_credits);//Additional Credits
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateGpaOfNextSemester();
                calculateCGPAIfSemesterGpaEqual();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        //What if Semester GPA =
        EditText whatIfSemesterGpaEditText = findViewById(R.id.what_if_semester_gpa);
        whatIfSemesterGpaEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        whatIfSemesterGpaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calculateCGPAIfSemesterGpaEqual();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //End of What if Semester GPA

        //End of Handle Target Gpa Calculator
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //Helper Functions
    public boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().isEmpty();
    }

    public String getEditTextString(EditText editText) {
        return editText.getText().toString();
    }

    public double getEditTextValue(EditText editText) {
        return Double.parseDouble("0" + getEditTextString(editText));
    }

    public void calculateCGPAIfSemesterGpaEqual() {
        EditText editText = findViewById(R.id.what_if_semester_gpa);//Semester GPA Edit Text
        String editTextString = getEditTextString(editText); // semesterGpa String
        TextView textView = findViewById(R.id.error_what_if_semester_gpa);//Validation Message for Semester Gpa EditText
        TextView requiredCurrentGpaTextView = findViewById(R.id.required_current_gpa);
        TextView requiredCurrentCreditsTextView = findViewById(R.id.required_current_credits);
        TextView requiredNextSemesterCredits = findViewById(R.id.required_next_semester_credits);

        //check if Semester Edit Text Is Empty or not
        if (!isEditTextEmpty(editText)) {
            //Semester GPA > 4  then Show Validation Message and Not Start Calculation
            if (getEditTextValue(editText) > 4) {
                textView.setText(errorMessage);
                textView.setVisibility(View.VISIBLE);
            } else {
                //Semester GPA <=4  then Start Calculate
                boolean cond = !getEditTextString(findViewById(R.id.current_gpa)).isEmpty()
                        && getEditTextValue(findViewById(R.id.current_gpa))<=4
                        && !getEditTextString(findViewById(R.id.current_credits)).isEmpty()
                        && !getEditTextString(findViewById(R.id.additional_credits)).isEmpty();
                if (getEditTextString(findViewById(R.id.current_gpa)).isEmpty()) {
                    requiredCurrentGpaTextView.setVisibility(View.VISIBLE);
                } else {
                    requiredCurrentGpaTextView.setVisibility(View.GONE);
                }
                if (getEditTextString(findViewById(R.id.current_credits)).isEmpty()) {
                    requiredCurrentCreditsTextView.setVisibility(View.VISIBLE);
                } else {
                    requiredCurrentCreditsTextView.setVisibility(View.GONE);
                }
                if (getEditTextString(findViewById(R.id.additional_credits)).isEmpty()) {
                    requiredNextSemesterCredits.setVisibility(View.VISIBLE);
                } else {
                    requiredNextSemesterCredits.setVisibility(View.GONE);
                }
                textView.setVisibility(View.GONE);
                textView = findViewById(R.id.cgpa_text_view);
                if (cond) {
                    double cgpa = getCumulativeGpa(getCurrentGpa(), getCurrentCredits(), getEditTextValue(editText), getAdditionalCredits());
                    String temp = String.valueOf("" + cgpa);
                    if (temp.length() > 5) temp = temp.substring(0, 5);
                    textView.setText(temp);
                }else{
                    textView.setText("0.0");
                }
            }
        } else {// if Semester GPA isEmpty so remove Validation Message
            textView.setVisibility(View.GONE);
            textView = findViewById(R.id.cgpa_text_view);
            textView.setText("0.0");
            requiredCurrentGpaTextView.setVisibility(View.GONE);
            requiredCurrentCreditsTextView.setVisibility(View.GONE);
            requiredNextSemesterCredits.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void rearrangeRows() {
        TableLayout tableLayout = (TableLayout) arrayOfViews.get(2);
        TableRow tableRow;
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            tableRow = (TableRow) tableLayout.getChildAt(i);
            TextView textView = (TextView) tableRow.getChildAt(0);
            textView.setText("#" + i);
        }
    }

    public double getCurrentGpa() {
        EditText editText = findViewById(R.id.current_gpa);
        String currGpaString = editText.getText().toString();
        return Double.parseDouble("0" + currGpaString);
    }

    public double getTargetGpa() {
        EditText editText = findViewById(R.id.target_gpa);
        String targetGpaString = editText.getText().toString();
        return Double.parseDouble("0" + targetGpaString);
    }

    public int getCurrentCredits() {
        EditText editText = findViewById(R.id.current_credits);
        String currentCredits = editText.getText().toString();
        return Integer.parseInt("0" + currentCredits);
    }

    public int getAdditionalCredits() {
        EditText editText = findViewById(R.id.additional_credits);
        String additionalCredits = editText.getText().toString();
        return Integer.parseInt("0" + additionalCredits);

    }

    public double getGpaOfNextSemester(double currGpa, double targetGpa, int currCredits, int addCredits) {
        int totalCredits = currCredits + addCredits;
        if(addCredits == 0) return 0;
        return (targetGpa * totalCredits - currGpa * currCredits) / addCredits;
    }

    //Need Handling
    @SuppressLint("SetTextI18n")
    public void calculateGpaOfNextSemester() {
        TextView gpaOfNextSemesterTextView = findViewById(R.id.gpa_of_next_semster_text_view);
        TextView hintTextView = findViewById(R.id.hint);
        int additionalShouldRegister;

        boolean condition = getCurrentGpa() <= 4 &&
                !getEditTextString(findViewById(R.id.current_gpa)).isEmpty()
                && getTargetGpa() <= 4 && !getEditTextString(findViewById(R.id.target_gpa)).isEmpty()
                && !getEditTextString(findViewById(R.id.current_credits)).isEmpty()
                && !getEditTextString(findViewById(R.id.additional_credits)).isEmpty();
        if (condition) {
            double gpaOfNextSemester = getGpaOfNextSemester(getCurrentGpa(), getTargetGpa(), getCurrentCredits(), getAdditionalCredits());
            //Check Gpa
            double top = getCurrentCredits() * (getCurrentGpa() - getTargetGpa());
            double bottom;
            if (gpaOfNextSemester > 4) {
                //calculate the credits should student register to achieve 4 gpa
                top = getCurrentCredits() * (getCurrentGpa() - getTargetGpa());
                if (getTargetGpa() == 4) bottom = 3.999 - 4;
                else bottom = getTargetGpa() - 4;
                additionalShouldRegister = (int) (top / bottom);
                gpaOfNextSemesterTextView.setText("");//Remove Text

                hintTextView.setText("Hint : To Achieve  " + getTargetGpa() + "  GPA ,You should register " + additionalShouldRegister + " Credits");
                hintTextView.setVisibility(View.VISIBLE);

            } else if (gpaOfNextSemester < 0) {
                additionalShouldRegister = (int) (top / (getTargetGpa()));
                gpaOfNextSemesterTextView.setText("");
                hintTextView.setText("Hint : To Achieve  " + getTargetGpa() + "  GPA ,You should register " + additionalShouldRegister + " Credits");
                hintTextView.setVisibility(View.VISIBLE);
            } else {
                String temp = String.valueOf(+gpaOfNextSemester);
                if (temp.length() > 4) temp = temp.substring(0, 4);
                gpaOfNextSemesterTextView.setText("GPA of Next Semester should equal  " + temp);
                hintTextView.setVisibility(View.GONE);
            }
        } else {
            gpaOfNextSemesterTextView.setText("");
            hintTextView.setVisibility(View.GONE);
        }
    }


    public double getSemesterGpa() {
        if (getSemesterCredits() == 0) return getSumOfProductGradeAndCredit();
        return getSumOfProductGradeAndCredit() / getSemesterCredits();
    }

    public double getCumulativeGpa(double prevGpa, int totalCredits, double semesterGpa, int semesterCredits) {
        double top = semesterGpa * semesterCredits + prevGpa * totalCredits;
        int bottom = totalCredits + semesterCredits;
        if (bottom == 0) return semesterGpa;
        return top / bottom;
    }

    public int getSemesterCredits() {
        TableLayout tableLayout = (TableLayout) arrayOfViews.get(2);
        TableRow tableRow;
        Spinner credit, grade;
        String selectedGrade;
        int totalCredits = 0;
        LinearLayout parentOfSpinner;
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            tableRow = (TableRow) tableLayout.getChildAt(i);
            parentOfSpinner = (LinearLayout) tableRow.getChildAt(2);//Credit dropdown
            credit = (Spinner) parentOfSpinner.getChildAt(0);
            parentOfSpinner = (LinearLayout) tableRow.getChildAt(1); //Grade Dropdown
            grade = (Spinner) parentOfSpinner.getChildAt(0);
            selectedGrade = grade.getSelectedItem().toString();
            if (!selectedGrade.isEmpty()) {
                selectedGrade = credit.getSelectedItem().toString();
                totalCredits += Double.parseDouble("0" + selectedGrade);
                tableRow.setBackgroundColor(Color.LTGRAY);
            } else {
                tableRow.setBackgroundColor(Color.TRANSPARENT);
            }
        }
        return totalCredits;
    }

    public double getSumOfProductGradeAndCredit() {
        TableLayout tableLayout = (TableLayout) arrayOfViews.get(2);
        TableRow tableRow;
        Spinner grade, credit;
        String selectedGrade, selectedCredit;
        double sumOfGradeProductCredit = 0;
        for (int i = 1; i < tableLayout.getChildCount(); i++) {
            tableRow = (TableRow) tableLayout.getChildAt(i);
            LinearLayout parentOfSpinner = (LinearLayout) tableRow.getChildAt(1);
            grade = (Spinner) parentOfSpinner.getChildAt(0);
            parentOfSpinner = (LinearLayout) tableRow.getChildAt(2);
            credit = (Spinner) parentOfSpinner.getChildAt(0);
            selectedGrade = grade.getSelectedItem().toString();
            selectedCredit = credit.getSelectedItem().toString();
            switch (selectedGrade) {
                case "A":
                    sumOfGradeProductCredit += 4 * Double.parseDouble("0" + selectedCredit);
                    break;
                case "-A":
                    sumOfGradeProductCredit += 3.67 * Double.parseDouble("0" + selectedCredit);
                    break;
                case "B+":
                    sumOfGradeProductCredit += 3.33 * Double.parseDouble("0" + selectedCredit);
                    break;
                case "B":
                    sumOfGradeProductCredit += 3 * Double.parseDouble("0" + selectedCredit);
                    break;
                case "C+":
                    sumOfGradeProductCredit += 2.67 * Double.parseDouble("0" + selectedCredit);
                    break;
                case "C":
                    sumOfGradeProductCredit += 2.33 * Double.parseDouble("0" + selectedCredit);
                    break;
                case "D":
                    sumOfGradeProductCredit += 2 * Double.parseDouble("0" + selectedCredit);
                    break;
                default:
                    sumOfGradeProductCredit += 0 * Double.parseDouble("0" + selectedCredit);
            }
        }
        return sumOfGradeProductCredit;
    }

    public int getTotalCredits() {
        return (getSemesterCredits() + getPrevTotalCredits());
    }

    public int getPrevTotalCredits() {
        LinearLayout parentOfEditTexts = (LinearLayout) arrayOfViews.get(1);
        totalCreditsEditText = (EditText) parentOfEditTexts.getChildAt(2);
        String totalCreditsString = totalCreditsEditText.getText().toString();
        return Integer.parseInt("0" + totalCreditsString);
    }

    public double getPrevGpa() {
        LinearLayout parentOfEditTexts = (LinearLayout) arrayOfViews.get(1);
        prevGpaEditText = (EditText) parentOfEditTexts.getChildAt(0);
        String prevGpaValue = prevGpaEditText.getText().toString();
        return Double.parseDouble("0" + prevGpaValue);
    }

    //End of Helper Functions
    @SuppressLint("SetTextI18n")
    public void calculate() {
        double cgpa;
        int totalCredits;
        if (getPrevGpa() <= 4 && !getEditTextString(prevGpaEditText).isEmpty() && !getEditTextString(totalCreditsEditText).isEmpty()) {
            cgpa = getCumulativeGpa(getPrevGpa(), getPrevTotalCredits(), getSemesterGpa(), getSemesterCredits());
            totalCredits = getTotalCredits();
        } else {
            cgpa = getSemesterGpa();
            totalCredits = getSemesterCredits();
        }
        double semesterGpa = getSemesterGpa();
        TextView textView = findViewById(R.id.cumulative_gpa_text_view);//CGP textView
        textView.setText("" + cgpa);
        textView = findViewById(R.id.cumulative_credits_hours_text_view);//Total Credits TextView
        textView.setText("" + totalCredits);
        textView = findViewById(R.id.semster_gpa_text_view);//Semester Gpa TextView
        textView.setText("" + semesterGpa);
        textView = findViewById(R.id.semster_credits_hours_text_view);//Semester Credits TextView
        textView.setText("" + getSemesterCredits());
    }

    @SuppressLint("SetTextI18n")
    public void addRow() {
        TableLayout tableLayout = (TableLayout) arrayOfViews.get(2);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TableRow tableRow = (TableRow) inflater.inflate(R.layout.resuable_row, tableLayout, false);
        //Set text
        TextView textView = (TextView) tableRow.getChildAt(0);
        textView.setText("#" + (tableLayout.getChildCount()));
        //Set Grade dropdown
        LinearLayout parentOfSpinner = (LinearLayout) tableRow.getChildAt(1);
        Spinner dropdown = (Spinner) parentOfSpinner.getChildAt(0);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Set Credits dropdown
        parentOfSpinner = (LinearLayout) tableRow.getChildAt(2);
        dropdown = (Spinner) parentOfSpinner.getChildAt(0);
        dropdown.setAdapter(adapter2);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Handle Delete Row Icon
        ImageView deleteIcon = (ImageView) tableRow.getChildAt(3);
        deleteIcon.setOnClickListener(view -> {
            if (tableLayout.getChildCount() > 2) {
                tableLayout.removeView(tableRow);
                calculate();
                rearrangeRows();
            }
        });
        tableLayout.addView(tableRow);
        calculate();
    }
}

