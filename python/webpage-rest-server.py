from flask import Flask, jsonify, abort, request, make_response
import sys
import os
import logging
import base64, requests, json

app = Flask(__name__)
app.config['LOGGING_LEVEL'] = logging.INFO

PORT = os.getenv('PORT', '7777')
GCLOUD_TARGET = os.getenv('GCLOUD_TARGET')

# Status Codes
HTTP_200_OK = 200
HTTP_400_BAD_REQUEST = 400

@app.route('/')
def index():
    return app.send_static_file('index.html')

@app.route('/price/batch/<int:num_portfs>', methods=['GET'])
def price_batch(num_portfs):
    """
    Price a random batch of portfolios

    Returns timing and pricing information
    """

    if (num_portfs < 1 or num_portfs > 100):
        abort(HTTP_400_BAD_REQUEST, f"Batch size {num_portfs} not in range [1,100]")

    auth = request.headers.get('Authorization')

    req = requests.get(f"{GCLOUD_TARGET}/price/batch?size={num_portfs}",
                    headers={'Authorization': auth},
                    verify=False)
    
    return jsonify(req.text), 200

@app.route('/query/bond/<int:bond_id>', methods=['GET'])
def query_bond(bond_id):
    """
    Queries the database for information on a specific bond

    """
    if (bond_id < 1 or bond_id > 5000):
        abort(HTTP_400_BAD_REQUEST, f"Bond ID {bond_id}, not in range [1,5000]")

    auth = request.headers.get('Authorization')

    req = requests.get(f"{GCLOUD_TARGET}/query/bond?id={bond_id}",
                    headers={'Authorization': auth},
                    verify=False)
    
    return jsonify(req.text), 200

@app.route('/query/portfolio/<int:portf_id>', methods=['GET'])
def query_portfolio(portf_id):
    """
    Queries the database for information on a specific portfolio

    """
    if (portf_id < 1 or portf_id > 100000):
        abort(HTTP_400_BAD_REQUEST, f"Portfolio ID {portf_id} not in range [1,100000]")

    auth = request.headers.get('Authorization')

    req = requests.get(f"{GCLOUD_TARGET}/query/portfolio?id={portf_id}",
                    headers={'Authorization': auth},
                    verify=False)

    return jsonify(req.text), 200

def initialize_logging(log_level):
    """ Initialized the default logging to STDOUT """
    if not app.debug:
        print('Setting up logging...')
        # Set up default logging for submodules to use STDOUT
        # datefmt='%m/%d/%Y %I:%M:%S %p'
        fmt = '[%(asctime)s] %(levelname)s in %(module)s: %(message)s'
        logging.basicConfig(stream=sys.stdout, level=log_level, format=fmt)
        # Make a new log handler that uses STDOUT
        handler = logging.StreamHandler(sys.stdout)
        handler.setFormatter(logging.Formatter(fmt))
        handler.setLevel(log_level)
        # Remove the Flask default handlers and use our own
        handler_list = list(app.logger.handlers)
        for log_handler in handler_list:
            app.logger.removeHandler(log_handler)
        app.logger.addHandler(handler)
        app.logger.setLevel(log_level)
        app.logger.info('Logging handler established')

def main():
    print('***************************************')
    print("         PARABOND REST SERVICE         ")
    print('***************************************')
    initialize_logging(app.config['LOGGING_LEVEL'])
    if GCLOUD_TARGET == None:
        app.logger.info('No URI to Parabond cluster, shutting down')
        return
    app.run(host='0.0.0.0', port=int(PORT))


if __name__ == "__main__":
    main()

