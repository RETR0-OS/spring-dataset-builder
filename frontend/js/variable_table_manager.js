let rowCount = 2;

document.getElementById('variables_table').addEventListener('change', function(event) {
    checkCategoricalSelections(event);
    checkNumericalSelections(event);
});
function checkCategoricalSelections(select = null, flag = false) {
    const selects = document.querySelectorAll('.form-select');
    const categoricalCount = Array.from(selects).filter(select => select.value === 'categorical').length;
    
    if (categoricalCount > 1) {
        if (select) {
            select.value = 'numeric';
        }
        if (flag) {
            return true;
        }
        alert('Only one categorical variable is allowed');
        return false;
    }
    return false;
}

// document.getElementById('variables_table').addEventListener('change', checkNumericalSelections);
function checkNumericalSelections(select = null) {
    const selects = document.querySelectorAll('.form-select');
    const numericalCount = Array.from(selects).filter(select => select.value === 'numeric').length;
    
    if (numericalCount > 2) {
        if (select) {
            select.value = 'categorical';
        }
        alert('Only four numerical variables are allowed');
        return true;
    }
    return false;
}

// Event listener wrapper
document.getElementById('variables_table').addEventListener('change', function(event) {
    if (event.target.tagName === 'SELECT') {
        checkNumericalSelections(event.target);
    }
});


function addRow(){
    let table = document.getElementById("variables_table");
    let tableBody = table.getElementsByTagName("tbody")[0];
    let selectOptions = tableBody.getElementsByTagName("select");
    if (rowCount <5){
        rowCount++;
        let numericCount = 2;
        for (let i = 0; i<selectOptions.length; i++){
            if(selectOptions[i].value == "numeric"){
                numericCount++;
            }
        }
        let selectedChoice = "numeric";
        let notSelectedChoice = "categorical";
        if(numericCount > 3){
            selectedChoice = "categorical";
            notSelectedChoice = "numeric";
        }
        tableBody.innerHTML +=`<tr>
                            <td contenteditable="true" id="variable-${rowCount}" class="dataset_variable" name="variable-${rowCount}">Variable ${rowCount}</td>
                            <td>
                                <select class="form-select">
                                    <option value="${selectedChoice}">${selectedChoice.charAt(0).toUpperCase() + selectedChoice.slice(1)}</option>
                                    <option value="${notSelectedChoice}">${notSelectedChoice.charAt(0).toUpperCase() + notSelectedChoice.slice(1)}</option>
                                </select>
                            </td>
                            <td>
                                <button type="button" class="btn btn-danger" onclick="deleteRow(this)">Delete</button>
                            </td>
                        </tr>`;
        if(rowCount == 5){
            document.getElementById("add_row_button").disabled = true;
        }
    }
}

function deleteRow(row){
    let table = document.getElementById("variables_table");
    let tableBody = table.getElementsByTagName("tbody")[0];
    tableBody.removeChild(row.parentNode.parentNode);
    rowCount--;
    if(rowCount < 5){
        document.getElementById("add_row_button").disabled = false;
    }
}

function checkForm(){
    let tableBody = document.getElementById("variables_table");
    let variable_names = document.getElementsByClassName("dataset_variable");
    let selectOptions = tableBody.getElementsByTagName("select");

    for (let i = 0; i<selectOptions.length; i++){
        if(variable_names[i+2].innerText == ""){
            alert("Variable names cannot be empty");
            return false;
        }
        if(selectOptions[i].value == "categorical"){
            if(checkCategoricalSelections(null, true)){
                alert("Only one categorical variable is allowed");
                return false;
            }
        }
        if(selectOptions[i].value == "numeric"){
            if(checkNumericalSelections(null)){
                alert("Only four numerical variables are allowed");
                return false;
            }
        }

        for (let j = i+1; j<selectOptions.length; j++){
            if(variable_names[i+2].innerText == variable_names[j+2].innerText){
                alert("Variable names must be unique");
                return false;
            }
        }
    }
    return true;
}

function submitForm(){
    if(checkForm()){
        let request = {};
        let numeric_variables = ["X Input", "Y Input"];
        let categorical_variable = [];

        let variables = document.getElementsByClassName("dataset_variable");
        let selectorOptions = document.getElementsByTagName("select");

        for(let i=0; i<selectorOptions.length; i++){
            if(selectorOptions[i].value == "numeric"){
                numeric_variables.push(variables[i+2].innerText); 
            }
            else if(selectorOptions[i].value == "categorical"){
                categorical_variable.push(variables[i+2].innerText);
            }
        }
        request.numerics = numeric_variables;
        request.categorical = categorical_variable;
        request.dataset_title = document.getElementById("dataset_title").value;
        request.dataset_description = document.getElementById("dataset_description").value;
        console.log(request);
        sessionStorage.setItem("user_prefs", JSON.stringify(request));

        window.location.href = "dataset_info.html";
    }
}