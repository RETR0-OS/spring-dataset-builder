let x_min = 0;
let x_max = 0;
let y_min = 0;
let y_max = 0;
let extra_numeric_settings = {};
let categorical_settings = {};

let chart = null;

const graphCanvas = document.getElementById("graphCanvas");
const ctx = graphCanvas.getContext("2d");

let brushSelected = false;
let eraseSelected = false;

let brushBtn = document.getElementById("brush-btn");
let eraseBtn = document.getElementById("erase-btn");
let brush_size = document.getElementById("brushSize");
let erase_size = document.getElementById("eraseSize");

let numeric_1 = document.getElementById(`var1Slider`);
let numeric_2 = document.getElementById(`var2Slider`);
let numPoints = document.getElementById(`numPointsSlider`);
let category_select = document.getElementById(`classSelector`);

let colors = ["rgba(255, 0, 0, ", "rgba(0, 255, 0, ", "rgba(0, 0, 255, ", "rgba(255, 255, 0, ", "rgba(0, 255, 255, "];

let dataset_settings = JSON.parse(sessionStorage.getItem("dataset_settings"));

let backend_url = "http://localhost:8080";

function load_data_options() {
    if (!dataset_settings || !dataset_settings.numeric_variables || !dataset_settings.categorical_variables) {
        return;
    }

    // Assign x and y min/max
    x_min = dataset_settings.numeric_variables["X_Input"]?.min || 0;
    x_max = dataset_settings.numeric_variables["X_Input"]?.max || 0;
    y_min = dataset_settings.numeric_variables["Y_Input"]?.min || 0;
    y_max = dataset_settings.numeric_variables["Y_Input"]?.max || 0;

    // Store additional numeric and categorical settings
    extra_numeric_settings = { ...dataset_settings.numeric_variables };
    categorical_settings = { ...dataset_settings.categorical_variables };

    // Remove X and Y from extra settings
    delete extra_numeric_settings["X_Input"];
    delete extra_numeric_settings["Y_Input"];
}

function draw_graph() {
    chart = new Chart(ctx, {
        type: "scatter", // Use scatter for x and y axes
        data: { datasets: [{
                data: [], // Start with an empty dataset
                pointStyle: [], // Set the shape of the data points
                pointRadius: [], // Set the size of the data points
                pointBackgroundColor: [], // Set the fill color
                pointBorderColor: [], // Set the border color
                pointBorderWidth: 1, // Set the border width }] }, // Empty dataset
            }],
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                x: {
                    type: "linear",
                    position: "bottom",
                    title: {
                        display: true,
                        text: "X Input",
                    },
                    min: Number(x_min),
                    max: Number(x_max),
                },
                y: {
                    type: "linear",
                    title: {
                        display: true,
                        text: "Y Input",
                    },
                    min: Number(y_min),
                    max: Number(y_max),
                },
            },
            plugins: {
                beforeDraw: (chart) => adjustOverlayCanvas(chart),
            },
        },
    });
}

function load_tools(){
    //load numeric variables
    let count = 1;
    if (Object.keys(extra_numeric_settings).length > 0){
        Object.keys(extra_numeric_settings).forEach((key) => {
            let setting_label = document.getElementById(`var${count}Label`);
            setting_label.innerText = key;
            let setting_slider = document.getElementById(`var${count}Slider`);
            setting_slider.min = extra_numeric_settings[key].min;
            setting_slider.max = extra_numeric_settings[key].max;
            count++;
        });
    }
    if(count == 2){
        let setting_slider = document.getElementById(`var2Slider`);
        setting_slider.disabled = true;
    }
    else if(count == 1){
        let setting_slider = document.getElementById(`var1Slider`);
        setting_slider.disabled = true;
        setting_slider = document.getElementById(`var2Slider`);
        setting_slider.disabled = true;
    }
    
    
    //load categorical variables
    if (Object.keys(categorical_settings).length > 0){
        let className = Object.keys(categorical_settings)[0];
        let class_label = document.getElementById("classLabel");
        class_label.innerText = className;
        let class_select = document.getElementById("classSelector");
        Object.keys(categorical_settings[className]).forEach((key) => {
            let option = document.createElement("option");
            option.value = categorical_settings[className][key];
            option.text = key;
            class_select.appendChild(option);
        });
    }     
    else{
        let class_select = document.getElementById("classSelector");
        class_select.disabled = true;
    }
}

function brushMode(){
    updateBrushSize(Number(brush_size.value));
    brushSelected = true;
    eraseSelected = false;

    brushBtn.classList.remove("btn-outline-primary");
    brushBtn.classList.add("btn-primary");
    eraseBtn.classList.remove("btn-danger");
    eraseBtn.classList.add("btn-outline-danger");
}

function generateRandomPoints(xClick, yClick, brushRadius, shape, ptRadius, ptOpacity, number_of_points){
    for (let i = 0; i < number_of_points; i++) {
        let x = chart.scales.x.getValueForPixel(xClick + Math.pow(-1, Math.floor(Math.random() * 2)) * Math.random() * (brushRadius));
        let y = chart.scales.y.getValueForPixel(yClick + Math.pow(-1, Math.floor(Math.random() * 2)) * Math.random() * (brushRadius));
        let color = colors[category_select.selectedIndex] + ptOpacity + ")";
        let borderColor = "rgba(0, 0, 0, 1)";
        let borderWidth = 1;
        // console.log(yClick);

        chart.data.datasets[0].data.push({x: x, y: y});
        chart.data.datasets[0].pointStyle.push(shape);
        chart.data.datasets[0].pointRadius.push(ptRadius);
        chart.data.datasets[0].pointBackgroundColor.push(color);
        chart.data.datasets[0].pointBorderColor.push(borderColor); 
    }
}

function calculateNewPoints() {
    let brush_radius = Number(brush_size.value);
    let point_radius = 5;
    let opacity = 1;
    let point_shape = "circle";
    if(!numeric_1.disabled){
        point_radius = 5 + ((numeric_1.value * 9.5) / (numeric_1.max - numeric_1.min)); //Sets point radius to be between 5 and 100.
        // console.log(point_radius);
    }
    let area = Math.floor((Math.PI * Math.pow(brush_radius, 2)) / ((point_radius * 2)+15));
    if(!numeric_2.disabled){
        opacity = (numeric_2.value - numeric_2.min) / (numeric_2.max - numeric_2.min); //Sets opacity to be between 0 and 1.
    }
    if(!category_select.disabled){
        point_shape = category_select.value.toLowerCase();
        // console.log(point_shape);
    }

    let number_of_points = numPointsSlider.value;

    return [brush_radius, point_shape, point_radius, opacity, number_of_points];
}

function updateBrushSize(brushRadius){
    let cursor = document.getElementById("circularcursor");
    cursor.style.width = `${brushRadius*2}px`;
    cursor.style.height = `${brushRadius*2}px`;
}

function submitDataset(){
    let request = {};
    let column_names = ["X_Input", "Y_Input"];
    let col_names = Object.keys(extra_numeric_settings);
    column_names = column_names.concat(col_names);
    let usr_prefs = JSON.parse(sessionStorage.getItem("user_prefs"));
    request["dataset_title"] = usr_prefs["dataset_title"];
    request["dataset_description"] = usr_prefs["dataset_description"];
    request["numerical_column_names"] = column_names;
    request["categorical_column_name"] = Object.keys(categorical_settings)[0];
    request["data_points"] = chart.data.datasets[0].data;
    request["data_bg_color"] = chart.data.datasets[0].pointBackgroundColor;
    request["point_radius"] = chart.data.datasets[0].pointRadius;
    request["point_class"] = chart.data.datasets[0].pointStyle;
    request["categorical_keys"] = {};
    let scales = [];
    let numeric_scales = dataset_settings.numeric_variables;
    let keys = Object.keys(numeric_scales);
    for(let i=0; i<keys.length; i++){
        scales[i] = [numeric_scales[keys[i]].min, numeric_scales[keys[i]].max];
    }
    if(request["categorical_column_name"] != null){
        let categorical = dataset_settings.categorical_variables[request["categorical_column_name"]];
        let categorical_keys = Object.keys(categorical);
        let reverse_map = {};
        for(let i=0; i<categorical_keys.length; i++){

            reverse_map[categorical[categorical_keys[i]].toLowerCase()] = categorical_keys[i];
        }
        request.categorical_keys = reverse_map;
    }
    request["scales"] = scales;
    fetch(`${backend_url}/api/v1/datasets/add/new/`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(request),
    }).then((response) => response.json()).then((json) => console.log(json));
    // console.log(request);

}

function eraseSelectedData(mouseX, mouseY){
    let data = chart.data.datasets[0].data;
    let pointRadius = chart.data.datasets[0].pointRadius;
    let pointStyle = chart.data.datasets[0].pointStyle;
    let pointBackgroundColor = chart.data.datasets[0].pointBackgroundColor;
    let pointBorderColor = chart.data.datasets[0].pointBorderColor;
    let pointBorderWidth = chart.data.datasets[0].pointBorderWidth;

    for (let i = 0; i < data.length; i++) {
        let x = chart.scales.x.getPixelForValue(data[i].x);
        let y = chart.scales.y.getPixelForValue(data[i].y);

        let distance = Math.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2));
        if (distance <= erase_size.value) {
            data.splice(i, 1);
            pointRadius.splice(i, 1);
            pointStyle.splice(i, 1);
            pointBackgroundColor.splice(i, 1);
            pointBorderColor.splice(i, 1);
            i--;
        }
    }
}

function eraseMode(){
    updateBrushSize(Number(erase_size.value));
    
    brushSelected = false;
    eraseSelected = true;

    eraseBtn.classList.remove("btn-outline-danger");
    eraseBtn.classList.add("btn-danger");
    brushBtn.classList.remove("btn-primary");
    brushBtn.classList.add("btn-outline-primary");
}

// Ensure functions run in the correct order
load_data_options();
draw_graph();
load_tools();

graphCanvas.addEventListener("click", (event) => {
    const rect = graphCanvas.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const mouseY = event.clientY - rect.top;

    // Convert pixel coordinates to data coordinates
    // const xValue = chart.scales.x.getValueForPixel(mouseX);
    // const yValue = chart.scales.y.getValueForPixel(mouseY);

    if (brushSelected){
        let options = calculateNewPoints(event, mouseX, mouseY);
        generateRandomPoints(mouseX, mouseY, options[0], options[1], options[2], options[3], options[4]);
    }
    else{
        eraseSelectedData(mouseX, mouseY);
    }
    chart.update(); // Re-render the chart
});

graphCanvas.addEventListener("mousemove", function(e) {
    const rect = graphCanvas.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;

    if (x >= 0 && x <= rect.width && y >= 0 && y <= rect.height) {
        // Mouse is inside canvas
        document.documentElement.style.setProperty('--x', e.clientX + 'px');
        document.documentElement.style.setProperty('--y', e.clientY + 'px');
        
        // Move the circular cursor
        const circularCursor = document.getElementById("circularcursor");
        circularCursor.style.left = `${x - circularCursor.offsetWidth / 2}px`;
        circularCursor.style.top = `${y - circularCursor.offsetHeight / 2}px`;
    }
});

