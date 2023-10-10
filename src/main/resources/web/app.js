let currentDate;
let socket;
let agentData = {};
let family_data = {};

function showData(name) {
    //var space = document.getElementById("agentInfo");
    //space.innerHTML = JSON.stringify(agentData[name], null, 2);
    //console.log("Showing data for:", agentData[name]);
    $(function () {
        document.getElementById("activeAgent").value =
            agentData[name]["name"];
        $("#agentInfo").JSONView(agentData[name]["state"]);
        $("#agentTasks").JSONView(agentData[name]["taskLog"]);
        $("#unblockDateList").JSONView(agentData[name]["unblockDateList"]);
    });

    const taskLog = JSON.parse(agentData[name]["taskLog"]);

    const taskCount = {};
    for (const key in taskLog) {
        const taskName = taskLog[key].task;
        if (!taskCount[taskName]) {
            taskCount[taskName] = 0;
        }
        taskCount[taskName]++;
    }

    const taskList = document.getElementById("taskList");
    taskList.innerHTML = "";

    for (const task in taskCount) {
        const listItem = document.createElement("li");
        listItem.className = "list-group-item";
        listItem.textContent = `${task}: ${taskCount[task]}`;
        taskList.appendChild(listItem);
    }

    createOptions(agentData[name]["state"]);
}

function addPeasantFamily(name) {
    let btn = document.createElement("button");
    btn.id = name;
    btn.className = "btn btn-primary btn-sm rounded-circle";
    btn.innerHTML = name;
    btn.style = "width:120px; font-size: xx-small;";
    btn.onclick = function () {
        showData(name);
    };
    document.getElementById("agentList").appendChild(btn);
    document
        .getElementById("activeAgent")
        .appendChild(new Option(name, name));
}

// Updates the agent's information on the UI based on the provided JSON data.
//
// Parameters:
// - jsonData: The JSON data containing the agent's information.
//
// Returns: None.
function updateAgent(jsonData) {
    let unSynchronized = "";
    //console.log("Updating agent:", jsonData);
    let agent = document.getElementById(jsonData.name);
    let state = JSON.parse(jsonData.state);
    let lands = {};
    lands[state.peasantFamilyLandAlias] = state.assignedLands;
    /*for (let farm in lands) {
        console.log("Farm:", farm);
        for (let land in lands[farm]) {
            console.log("  Land:", land, "Kind:", lands[farm][land].kind, "Used:", lands[farm][land].used);
        }
    }*/

    if (currentDate != null) {
        const currentDateParts = currentDate.split("/");
        const currentDateObj = new Date(
            currentDateParts[2],
            currentDateParts[1] - 1,
            currentDateParts[0]
        );
        const buttonDateParts = state.internalCurrentDate.split("/");
        const buttonDateObj = new Date(
            buttonDateParts[2],
            buttonDateParts[1] - 1,
            buttonDateParts[0]
        );
        // Calcular la diferencia en días
        const differenceInTime =
            buttonDateObj.getTime() - currentDateObj.getTime();
        const differenceInDays = differenceInTime / (1000 * 3600 * 24);
        if (differenceInDays < -7) {
            unSynchronized = " 🚫";
        } else {
            unSynchronized = "";
        }
    }

    agent.innerHTML = `${jsonData.name}<br>
                ${state.health} 💊<br>
                $${state.money.toLocaleString("es-CO")} 💰<br>
                ${state.internalCurrentDate} 📅${unSynchronized}<br>
                ${state.currentSeason} 🚜<br>
                ${state.peasantFamilyLandAlias} 🌱<br>`;  ;

    if (state.robberyAccount > 0) {
        agent.innerHTML = agent.innerHTML + " 🦹";
    }

    let color;

    if (state.currentActivity == "BLOCKED") {
        agent.className = "btn btn-secondary";
    } else if (state.health <= 0) {
        agent.className = "btn btn-secondary";
    } else if (state.health <= 30) {
        agent.className = "btn btn-warning";
    } else if (state.health <= 80) {
        agent.className = "btn btn-primary";
    } else {
        agent.className = "btn btn-success";
    }

    if (state.currentSeason=="PREPARATION"){
        color = "bisque";
    } else if (state.currentSeason=="PLANTING"){
        color = "yellow";
    } else if (state.currentSeason=="GROWING"){
        color = "green";
    } else if (state.currentSeason=="HARVEST"){
        color = "olive";
    }

    // Update land show
    //console.log("Updating land " + state.peasantFamilyLandAlias);
    let newTooltip = `
            ${jsonData.name}<br>
                ${state.peasantFamilyLandAlias}<br>
                ${state.health} 💊<br>
                $${state.money.toLocaleString("es-CO")} 💰<br>
                ${state.internalCurrentDate} 📅${unSynchronized}<br>
                ${state.currentSeason} 🚜
        `;
    modifyLand(state.peasantFamilyLandAlias, lands[state.peasantFamilyLandAlias], color, newTooltip);
    //console.log(state.assignedLands);

}

function createOptions(options) {
    const options_data = JSON.parse(options);
    //console.log("Creating options:", options_data);
    const form = document.getElementById("graphOptions");
    form.innerHTML = "";
    for (const [key, value] of Object.entries(options_data)) {
        //console.log(key, value);
        if (typeof value === "number") {
            const div = document.createElement("div");
            div.className = "form-check";

            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.id = "opt_" + key;
            checkbox.className = "form-check-input";
            checkbox.name = key;
            checkbox.value = value;

            const label = document.createElement("label");
            label.htmlFor = key;
            label.className = "form-check-label";
            label.textContent = key;

            // Agrega el checkbox y la etiqueta al formulario
            div.appendChild(checkbox);
            div.appendChild(label);
            form.appendChild(div);
        }
    }
}

if (window.WebSocket) {
    socket = new WebSocket("ws://0.0.0.0:8080/wpsViewer");
    socket.onmessage = function (event) {
        let prefix = event.data.substring(0, 2);
        let data = event.data.substring(2);
        switch (prefix) {
            case "j=":
                try {
                    let newAgentData = JSON.parse(data);
                    updateAgent(newAgentData);
                    if (!agentData) {
                        agentData = {};
                    }
                    agentData[newAgentData.name] = newAgentData;
                    if (!family_data[newAgentData.name]) {
                        family_data[newAgentData.name] = {};
                    }
                    let state = JSON.parse(newAgentData.state);
                    family_data[newAgentData.name][state.internalCurrentDate] = state;
                } catch (error) {
                    console.error("Error parsing JSON:", error);
                }
                break;
            case "q=":
                let number = parseInt(data, 10);
                for (let i = 1; i <= number; i++) {
                    addPeasantFamily("PeasantFamily_" + i);
                }
                break;
            case "d=":
                currentDate = data;
                document.getElementById("currentDate").innerHTML = data;
                break;
            case "m=":
                //document.getElementById("map").value = data;
                //console.log("Map:", data);
                break;
            case "s=":
                let jsonData = JSON.parse(data);
                document.getElementById("alive").innerHTML = jsonData.alive;
                document.getElementById("away").innerHTML = jsonData.away;
                document.getElementById("dead").innerHTML = jsonData.dead;
                break;
            default:
                console.log("Unknown message prefix:", prefix);
                break;
        }
        $("#agentList .btn-secondary").appendTo("#deadAgentList");
    };
    socket.onopen = function (event) {
        console.log("Web Socket opened!");
        socket.send("setup");
    };
    socket.onclose = function (event) {
        console.log("Web Socket closed.");
    };
} else {
    alert("Your browser does not support Websockets. (Use Chrome)");
}

function send(message) {
    if (!window.WebSocket) {
        return;
    }
    if (socket.readyState == WebSocket.OPEN) {
        socket.send(message);
    } else {
        console.log("The socket is not open.");
    }
}

function stringToHashCode(str) {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
        hash = (hash << 5) - hash + str.charCodeAt(i);
        hash = hash & hash;
    }
    return hash;
}
function hashCodeToColor(hash) {
    const r = (hash & 0xff0000) >> 16;
    const g = (hash & 0x00ff00) >> 8;
    const b = hash & 0x0000ff;
    return `rgba(${r}, ${g}, ${b}, 1)`;
}

const familySelect = document.getElementById("activeAgent");
let myChart;

function updateChart(familyName) {
    const selectedFamily = family_data[familyName];
    // Crear una función para convertir la fecha al formato YYYY/MM/DD para comparación
    const convertDate = (dateStr) => {
        const [day, month, year] = dateStr.split("/");
        return `${day}/${month}/${year}`;
    };

    // Obtener todas las claves (fechas) y ordenarlas
    const dates = Object.keys(selectedFamily).sort((a, b) => {
        return new Date(convertDate(a)) - new Date(convertDate(b));
    });

    const datasets = [];

    // Buscar todos los checkboxes con id que empiezan con "opt_"
    const checkboxes = document.querySelectorAll('[id^="opt_"]');
    checkboxes.forEach((checkbox) => {
        if (checkbox.checked) {
            // Obtén el nombre del campo del checkbox seleccionado, por ejemplo, "peasantLeisureAffinity" de "opt_peasantLeisureAffinity"
            const fieldName = checkbox.id.substring(4);

            if (
                dates.every(
                    (date) => selectedFamily[date][fieldName] !== undefined
                )
            ) {
                const data = dates.map((date) => selectedFamily[date][fieldName]);

                const hash = stringToHashCode(fieldName);
                const color = hashCodeToColor(hash);

                datasets.push({
                    label: fieldName,
                    data: data,
                    borderColor: color,
                    borderWidth: 1,
                });
            }
        }
    });

    if (!myChart) {
        // Crea el gráfico utilizando Chart.js.
        const ctx = document.getElementById("myChart").getContext("2d");
        myChart = new Chart(ctx, {
            type: "line",
            data: {
                labels: dates,
                datasets: datasets,
            },
            options: {
                animation: false,
                scales: {
                    x: {
                        type: "time",
                        time: {
                            parser: "DD/MM/YYYY",
                        },
                    },
                    y: {
                        beginAtZero: true,
                    },
                },
            },
        });
    } else {
        myChart.data.labels = dates;
        myChart.data.datasets = datasets;
        myChart.update();
    }
}

let familySelectValue;

document.addEventListener("DOMContentLoaded", (event) => {
    const familySelect = document.getElementById("activeAgent");

    if (!familySelect) {
        console.error('El elemento "activeAgent" no se encuentra en el DOM.');
        return;
    }

    familySelectValue = familySelect.value;
    //console.log("Valor inicial:", familySelectValue); // Para depuración

    familySelect.addEventListener("change", (event) => {
        familySelectValue = event.target.value;
        //console.log("Valor cambiado:", familySelectValue); // Para depuración

        try {
            updateChart(familySelectValue); // Actualizar el gráfico
            showData(familySelectValue); // Mostrar datos
        } catch (error) {
            console.error("Error en updateChart o showData:", error);
        }
    });
});

/*function periodicUpdate() {
  if (familySelectValue) {
    //console.log('Actualizando gráfico para:', familySelectValue);  // Para depuración
    try {
      updateChart(familySelectValue);
    } catch (error) {
      console.error("Error en updateChart:", error);
    }
  }
}*/

// Configuración del intervalo para actualizar cada 15 segundos
//setInterval(periodicUpdate, 5000);

let mymap = L.map("mapid").setView([9.9558349, -75.3062724], 14);
let landData = {};

L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
    attribution: "Map data © OpenStreetMap contributors",
}).addTo(mymap);

async function loadData() {
    try {
        let response = await fetch("world.json");
        let data = await response.text();
        let worldData = JSON.parse(data);
        worldData.forEach(function (fincaData, index) {
            let fillColor;
            //console.log(fincaData.kind);
            switch (fincaData.kind) {
                case "road":
                    fillColor = "SlateGrey";
                    break;
                case "water":
                    fillColor = "DarkBlue";
                    break;
                case "forest":
                    fillColor = "DarkGreen";
                    break;
                default:
                    fillColor = "Wheat";
            }

            let polygonOptions = {
                weight: 0.5,
                fillColor:  fillColor,
                fillOpacity: 0.5
            };

            let polygon = L.polygon(fincaData.coordinates, polygonOptions)
                .addTo(mymap)
                .bindTooltip(fincaData.name);
            landData[fincaData.name] = polygon;
            // Calcula el área en metros cuadrados
            //let area = L.GeometryUtil.geodesicArea(polygon.getLatLngs()[0]);
            //console.log("Área de " + fincaData.name + ": " + area + " m²");
        });
    } catch (err) {
        console.error("Error loading data", err);
    }
}


window.onload = loadData;

function modifyLand(name, landDataUse, color, newTooltip) {
    const borderStyleUnique = borderStyles[name];
    for (let land in landDataUse) {
        let landLayer = landData[land];
        if (landLayer) {
            landLayer.setStyle({
                fillColor: color,
                fillOpacity: 0.5,
                color: borderStyleUnique.color, // Color del borde
                weight: borderStyleUnique.weight, // Grosor del borde
                dashArray: borderStyleUnique.dashArray // Estilo de línea del borde
            });
            landLayer.on('click', function () {
                document.getElementById('agent-info-panel').innerHTML = newTooltip;
            });
        } else {
            console.error("No finca found with name:", name);
        }
    }
}


function getRandomColor() {
    const letters = '0123456789ABCDEF';
    let color = '#';
    for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}

function getRandomLineStyle() {
    const styles = ['', '5,5', '10,10', '5,10', '10,5'];
    return styles[Math.floor(Math.random() * styles.length)];
}

const borderStyles = {};
const sizes = ['large', 'medium', 'small'];

for (let i = 1; i <= 100; i++) {
    sizes.forEach(size => {
        const farmName = `farm_${i}_${size}`;
        borderStyles[farmName] = {
            color: getRandomColor(),
            weight: Math.floor(Math.random() * 0.7) + 1, // Grosor aleatorio entre 1 y 4
            dashArray: getRandomLineStyle()
        };
    });
}
