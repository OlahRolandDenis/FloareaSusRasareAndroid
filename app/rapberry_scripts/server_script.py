import network
import socket
import threading
import time

# functions from files
from wifi_connection import *
from client_script import *

# ip address and port to which the devices are connected
#HOST = '192.168.1.5'
HOST = '192.168.20.177'

PORT = 80

def run_server():
    # start server
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind((HOST, PORT))
    server_socket.listen(5) # listen for devices
    print(f'Server is listening on {HOST}:{PORT}')

    while True:
        try:
            print("listening for new client")

            # when a device is trying to connect, accept and save it
            client_socket, client_addr = server_socket.accept() 
            print(f'Client {client_addr[0]}:{client_addr[1]} connected')
            handle_client(client_socket) # control from client ( app )
        except KeyboardInterrupt:
            print('Shutting down server')
            server_socket.close()
            break

if __name__ == '__main__':
    if connectToWifi(): # on startup, connect to WiFi Address
        print("Connected to wifi! starting server")
        run_server()
    else:
        print("Could not connect to wifi")
        
