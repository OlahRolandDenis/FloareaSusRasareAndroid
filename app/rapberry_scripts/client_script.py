from machine import Pin
import time

class PlantParameter:
    def __init__(plant_parameter, name, value):
        plant_parameter.name = name
        plant_parameter.value = value

# defining some test parameters
test_params = [
    PlantParameter("temperature_param", "18*C"),
    PlantParameter("oxygen_param", "100%"),
    PlantParameter("humidity_param", "needs to be watered")
]

# Start listening on socket
led = Pin('LED', Pin.OUT) # The initial state of the LED

def handle_client(client_socket):
    global led_state
    connected = True
    while connected:
        try:
            data = client_socket.recv(1024)
            if not data:
                break
            message = data.decode('utf-8')
            print(f"Received message: {message} and now I am closing")  # Debug print
            if message == '1\n':
                led.on()
                print('LED is on')
            elif message == '0\n':
                led.off()
                print('LED is off')
            elif message == 'sayHi\n':
                led.on()
                time.sleep(2)
                led.off()
                print('LED is acting for sayHi command')
                print('I AM SAYING HIIIII!!!')
                
                # send all the info into a single string to the application ( each individual thing will be 'discovered' by a to be determined separator )
                for param in test_params:
                    client_socket.sendall(f"{param.name}: {param.value}   ")
            else:
                print('Invalid message')
            print("responding with ok")
            client_socket.sendall("THIS IS ME RESPONDING WITH OK hehe\n")
            print("responded with ok")
            # connected = False
        except OSError:
            # Client disconnected
            print("client is dissconnected")
            connected = False
            break
    client_socket.close()
    print("closed client")