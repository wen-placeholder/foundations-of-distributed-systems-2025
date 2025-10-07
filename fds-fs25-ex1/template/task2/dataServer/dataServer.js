const grpc = require('@grpc/grpc-js');
const protoLoader = require('@grpc/proto-loader');
const path = require('path');

// Load the protobuf file
const PROTO_PATH = path.join(__dirname, 'dservice.proto');
const packageDefinition = protoLoader.loadSync(PROTO_PATH, {});
const dataProto = grpc.loadPackageDefinition(packageDefinition).DATA;

// In-memory storage for users and their data
const users = {};

function registerUser(call, callback) {
    const { username, password } = call.request;

    if (users[username]) {
        return callback(null, { success: false }); // User already exists
    }

    users[username] = { password: password, data: null, passcode: null };
    console.log(`A user is defined (username:${username}, password: ${password})`);
    callback(null, { success: true });
}

// Function to store user data
function storeData(call, callback) {
    const { username, password, msg } = call.request;

    const user = users[username];
    if (!user || user.password !== password) {
        return callback(null, { success: false }); // Invalid credentials
    }

    user.data = msg; // Store the message
    callback(null, { success: true });
}

// Function to generate a passcode
function genPasscode(call, callback) {
    const { username, password } = call.request;

    const user = users[username];
    if (!user || user.password !== password) {
        return callback(null, { code: '' }); // Invalid credentials
    }

    const passcode = Math.random().toString(36).substring(2, 10); // Generate a simple passcode
    user.passcode = passcode; // Store the passcode
    callback(null, { code: passcode });
}

// Function to get data using username and password
function getData(call, callback) {
    const { username, password } = call.request;

    const user = users[username];
    if (!user || user.password !== password) {
        return callback(null, { msg: '' }); // Invalid credentials
    }

    callback(null, { msg: user.data });
}

// Function to get authorized data using a passcode
function getAuthData(call, callback) {
    const { code } = call.request;

    const user = Object.values(users).find(u => u.passcode === code);
    if (user) {
        user.passcode = null;
        callback(null, { msg: user.data });
    } else {
        callback(null, { msg: '' }); // Invalid passcode
    }
}

// Main function to set up the server
function main() {
    const server = new grpc.Server();

    // Register the service methods
    server.addService(dataProto.DB.service, {
        RegisterUser: registerUser,
        StoreData: storeData,
        GenPasscode: genPasscode,
        GetData: getData,
        GetAuthData: getAuthData,
    });

    const PORT = '0.0.0.0:50051';
    server.bindAsync(PORT, grpc.ServerCredentials.createInsecure(), (error, port) => {
        if (error) {
            console.error('Error binding server:', error);
            return;
        }
        console.log(`Data server listening on port//${port}`);
    });
}

main();
