let api_base_url = 'http://localhost:8080/api/';
let all_datasets_endpoint = 'v1/datasets/list/all/';

function fetch_datasets(){
    return fetch(api_base_url + all_datasets_endpoint)
    .then(response => {
        if (!response.ok) {
            throw new Error('Error fetching datasets');
        }
        return response.json();
    })
    .then(data => {
        return data.datasets;
    });
}

// Example of how to populate cards with dataset information
function createDatasetCard(dataset) {
    return `
        <div class="col-md-4">
            <div class="card h-100 dataset-card">
                <div class="card-body">
                    <h5 class="card-title">${dataset.name}</h5>
                    <p class="card-text">${dataset.description}</p>
                </div>
                <div class="card-footer bg-transparent border-0">
                    <button class="btn btn-primary btn-sm" onclick=window.location="http://127.0.0.1:5500/frontend/datasets/dashboard/view_dataset.html?id=${dataset.id}">Open</button>
                    <button class="btn btn-outline-danger btn-sm" onclick=delete_dataset(${dataset.id})>Delete</button>
                </div>
            </div>
        </div>
    `;
}

// Example of how to handle the JSON data
// Replace this with your actual API call
async function loadDatasets() {
    try {
        const datasets = await fetch_datasets();

        const container = document.getElementById('datasetContainer');
        
        for (let i = datasets.length-1; i >= 0; i--) {
            container.innerHTML += createDatasetCard(datasets[i]);
        }
    } catch (error) {
        console.error('Error loading datasets:', error);
    }
}

function delete_dataset(id){
    if(id == null){
        throw new Error('No dataset ID provided');
    }
    let url = api_base_url + `v1/datasets/delete/${id}`;
    fetch(url, 
        {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            }
        }
    ).then(response => {
        if (response.status == 404) {
            throw new Error('Dataset not found!');
        }
        else if(!response.ok){
            throw new Error('Error deleting dataset');
        }
        return response.json();
    }).then(
        window.location.href = 'dashboard.html'
    )
}

window.onload = loadDatasets;