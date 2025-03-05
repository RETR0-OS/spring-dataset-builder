let api_reponse = {}
function load_user_options(){
    let prefs = sessionStorage.getItem("user_prefs");
    if (prefs){
        prefs = JSON.parse(prefs);
        api_reponse = prefs;
        console.log(api_reponse);
    }
}

num_cat_classes = 0;

function populateForm(){
    try {
        let numeric_section = document.getElementById("numeric_variable_controls");
        let categorical_section = document.getElementById("categorical_variable_controls");

        api_reponse.numerics.forEach(numeric => {
            if (numeric != "X Input" && numeric != "Y Input"){
                numeric_section.innerHTML += `
                    <div class="border p-2 mb-2 rounded">
                        <div class="mb-3">
                            <label for="${numeric}-min" class="form-label">${numeric} Minimum</label>
                            <input type="text" class="form-control" id="${numeric}-min" name="${numeric}-min" placeholder="0" required>
                        </div>
                        <div class="mb-3">
                            <label for="${numeric}-max" class="form-label">${numeric} Max</label>
                            <input type="text" class="form-control" id="${numeric}-max" name="${numeric}-max" placeholder="100" required>
                        </div>
                    </div>`;
            }
        });

        if (api_reponse.categorical.length > 0){
            categorical_section.innerHTML += `
                <div class="mb-3">
                    <label for="${api_reponse.categorical[0]}-classes" class="form-label">Number of ${api_reponse.categorical[0]} Classes</label>
                    <input type="text" class="form-control" id="${api_reponse.categorical[0]}-classes" name="${api_reponse.categorical[0]}-classes" placeholder="Enter a number between 2 and 5" onchange="categoricalClassShapeSelection(event)" value=""> 
                </div>`;
            num_cat_classes = 2;
        }
        else{
            categorical_section.innerHTML += `
                <div class="mb-3">
                    <p>No categorical variables</p>
                </div>`;
        }
    } catch (error) {
        alert("An error occurred while populating the form: " + error.message);
    }
}

function categoricalClassShapeSelection(event){
    try {
        let numClasses = parseInt(event.target.value);
        num_cat_classes = numClasses;
        let selectHTML = "";
        if (!isNaN(numClasses) && numClasses <= 5 && numClasses >= 2){
            let parentNode = event.target.parentNode;
            let shapeOptions = ['Circle', 'Rect', 'Triangle', 'Star', 'Cross'];

            // Remove existing selection boxes
            const existingSelections = parentNode.querySelector('div.mb-3.mt-2');
            if (existingSelections) {
                existingSelections.remove();
            }

            for (let i = 0; i < numClasses; i++) {
                selectHTML += `
                    <div class="mb-3 p-2 border rounded">
                        <input type="text" class="form-control mb-2" placeholder="Enter class name" id="${event.target.id}-class-${i+1}" required>
                        <select class="form-select" id="${event.target.id}-shape-${i+1}">
                            <option selected value="" required>Select Shape</option>
                            ${shapeOptions.map(shape => `<option value="${shape}">${shape}</option>`).join('')}
                        </select>
                    </div>`;
            }

            parentNode.innerHTML += `
                <div class="mb-3 mt-2">
                    <label for="${event.target.id}-class-shape" class="form-label">Class Shapes</label>
                    ${selectHTML}
                </div>`;
        } else {
            alert("Number of classes must be a number between 2 and 5");
        }
        event.target.value = numClasses;
    } catch (error) {
        alert("An error occurred while selecting class shapes: " + error.message);
    }
}

function submitForm(event){
    try {
        event.preventDefault(); // Prevent the default form submission

        const formData = {
            numeric_variables: {},
            categorical_variables: {}
        };

        console.log("Collecting X-Y data")
        // Collect X and Y input data
        formData.numeric_variables["X_Input"] = {min: document.getElementById("X-min").value, max: document.getElementById("X-max").value};
        formData.numeric_variables["Y_Input"] = {min: document.getElementById("Y-min").value, max: document.getElementById("Y-max").value};
        
        console.log("Collecting numeric data");
        // Collect numeric variables data
        api_reponse.numerics.forEach(numeric => {
            if (numeric != "X Input" && numeric != "Y Input"){
                formData.numeric_variables[numeric] = {
                    min: document.getElementById(`${numeric}-min`).value,
                    max: document.getElementById(`${numeric}-max`).value
                };
            }
        });

        console.log("Collecting categorical data");
        // Collect categorical variables data
        if (api_reponse.categorical.length > 0) {
            const categorical = api_reponse.categorical[0];
            const numClasses = document.getElementById(`${categorical}-classes`).value;
            const classes = {};
            let shapesValidator = ["Circle", "Rect", "Triangle", "Star", "Cross"];
            
            for (let i = 0; i < num_cat_classes; i++) {
                const className = document.getElementById(`${categorical}-classes-class-${i+1}`).value;
                const shape = document.getElementById(`${categorical}-classes-shape-${i+1}`).value;
                if (shape === ""){
                    alert("Please select a shape for each class");
                    return;
                }
                else if (!shapesValidator.includes(shape)){
                    alert("Please select a unique shape for each class");
                    return;
                }
                else{
                    shapesValidator = shapesValidator.filter(s => s !== shape);
                }
                classes[className] = shape;
            }
            
            formData.categorical_variables[categorical] = classes;
        }

        sessionStorage.setItem("dataset_settings", JSON.stringify(formData));
        window.location.href = "draw_dataset.html";
    } catch (error) {
        alert("An error occurred while submitting the form: " + error.message);
    }
}
load_user_options();    
populateForm();