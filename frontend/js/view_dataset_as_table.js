let api_base_url = 'http://localhost:8080/api/';

function get_query_params(param){
    const urlParams = new URLSearchParams(window.location.search);
      return urlParams.get(param);
}

function fetch_dataset(id){
    console.log('Fetching dataset with id:', id);
    let dataset_endpoint = `v1/datasets/get/${id}/`;
    return fetch(api_base_url + dataset_endpoint, 
        {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            }
        }
    )
    .then(response => {
        if (response.status == 404) {
            throw new Error('Dataset not found!');
        }
        else if(!response.ok){
            throw new Error('Error fetching dataset');
        }
        return response.json();
    })
    .then(data => {
        return data.dataset;
    });
}

// Example of how to handle the JSON data
// Replace this with your actual API call
async function loadDataset() {
    try {
        const id = get_query_params('id');
        if(id == null){
            throw new Error('No dataset ID provided');
        }
        const dataset = await fetch_dataset(id);

        const headers_container = document.getElementById('header-row');
        const t_body_container = document.getElementsByTagName('tbody')[0];
        
        const page_title = document.getElementById('datasetTitle');
        page_title.innerText = dataset.name;

        let headers = dataset.headers.split(',');
        let rows = dataset.data.split('\n');
        
        for (let i = 0; i < headers.length; i++) {
            headers_container.innerHTML += `<th>${headers[i]}</th>`;
        }
        for (let i = 0; i< rows.length; i++){
            let row = rows[i].split(',');
            let row_html = '';
            for (let j = 0; j < row.length; j++){
                row_html += `<td>${row[j]}</td>`;
            }
            t_body_container.innerHTML += `<tr>${row_html}</tr>`;
        }
    } catch (error) {
        console.error('Error loading datasets:', error);
    }
}

window.onload = loadDataset;