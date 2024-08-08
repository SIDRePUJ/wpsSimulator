import paramiko
import threading
import time
import os
from datetime import datetime

# Configuración de los servidores y credenciales
servers = [
    "wpsmain",
    "wps01",
    "wps02",
    "wps03"
]
user = "sistemas"
ssh_key_path = "/home/sistemas/.ssh/id_rsa"  # Ruta a tu clave privada

# Tiempo de espera por experimento
waiting_time = 30000
# Parámetros a variar
agents_list = [25, 100, 200, 500]
land_list = [12]

# Directorio del ejecutable
jar_path = "/home/sistemas/wpsim/wpsSimulator/target/wpsim-1.0.jar"

# Evento para señalizar la finalización de los hilos
finished_events = [threading.Event() for _ in range(len(servers))]

def execute_command(server, user, ssh_key_path, command, index, timeout, work_dir):
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    try:
        ssh.connect(server, username=user, key_filename=ssh_key_path)
    except paramiko.ssh_exception.SSHException as e:
        print(f"Error al conectarse al servidor {server} con la clave privada: {e}")
        return False

    # Crear el directorio de trabajo y copiar el archivo jar
    ssh.exec_command(f"mkdir -p {work_dir}")
    ssh.exec_command(f"cp {jar_path} {work_dir}/")
    full_command = f"cd {work_dir} && {command}"

    stdin, stdout, stderr = ssh.exec_command(full_command)

    start_time = time.time()
    while not stdout.channel.exit_status_ready():
        if time.time() - start_time > timeout:
            print(f"Comando en {server} se demoró más de lo esperado. Cancelando...")
            ssh.close()
            return False
        time.sleep(1)

    if stdout.channel.recv_exit_status() == 0:
        print(f"Comando ejecutado correctamente en {server}")
    else:
        print(f"Error en la ejecución del comando en {server}")

    for line in stdout:
        print(line.strip('\n'))
    for line in stderr:
        print(line.strip('\n'))

    ssh.close()
    finished_events[index].set()
    return True

# Comando base
base_command = "java -XX:+UseG1GC -XX:MaxMetaspaceSize=4096m -Xmx1024m -Xmx60g -jar wpsim-1.0.jar --money 3000000 --land {land} --personality 0.0 --tools 20 --seeds 50 --water 5000 --irrigation 1 --emotions 1 --training 0 --nodes 3 --mode {mode} --agents {agents} 2>&1 | grep -v 'FINE' | grep UPDATE"

def run_experiments():
    timestamp = datetime.now().strftime("%Y%m%d%H%M")
    experiment_id = 1
    for land in land_list:
        for agents in agents_list:
            threads = []
            finished_events = [threading.Event() for _ in range(len(servers))]

            def timer_callback():
                if not all(event.is_set() for event in finished_events):
                    print("Un hilo se ha demorado más de 5 minutos. Reiniciando simulación.")
                    for thread in threads:
                        if thread.is_alive():
                            print(f"Terminando hilo {thread.name}")
                            # Aquí no podemos forzar la terminación del hilo, solo podemos esperar a que termine.

                    run_experiments()

            timer = threading.Timer(waiting_time, timer_callback)
            timer.start()

            for i, server in enumerate(servers):
                mode = server  # El modo es el mismo nombre del servidor
                work_dir = f"/home/sistemas/experiments/exp_{experiment_id}_{timestamp}"
                command = base_command.format(land=land, mode=mode, agents=agents)
                print(f"Ejecutando en {server}: {command} \n Directorio {work_dir}")

                thread = threading.Thread(target=execute_command, args=(server, user, ssh_key_path, command, i, waiting_time, work_dir))
                threads.append(thread)
                thread.start()

                # Esperar 5 segundos antes de iniciar el siguiente servidor
                if i < len(servers) - 1:
                    time.sleep(2)

            # Esperar a que todos los hilos terminen antes de pasar a la siguiente combinación de parámetros
            for thread in threads:
                thread.join()

            # Cancelar el temporizador si todos los hilos terminan antes de los 5 minutos
            timer.cancel()

            # Incrementar el ID del experimento
            experiment_id += 1

            # Esperar que todos los comandos terminen antes de iniciar el segundo experimento
            time.sleep(60)  # Ajustar según el tiempo esperado de ejecución del comando

            # Segundo experimento (si se requiere)
            # command = base_command.format(land=land, mode=mode, agents=agents)
            # execute_command(server, user, password, command)

if __name__ == "__main__":
    run_experiments()
