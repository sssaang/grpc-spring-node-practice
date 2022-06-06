const express = require('express')
const bodyParser = require('body-parser')
const axios = require('axios')
const app = express()
const port = 3333

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

const PROTO_PATH = "../proto/sample.proto";

var grpc = require("grpc");
var protoLoader = require("@grpc/proto-loader");

var packageDefinition = protoLoader.loadSync(PROTO_PATH, {
    keepCase: true,
    longs: String,
    enums: String,
    arrays: true
});

const SampleService = grpc.loadPackageDefinition(packageDefinition).me.sssaang.sample.SampleService;
const client = new SampleService(
    "localhost:6565",
    grpc.credentials.createInsecure()
);

const { v4: uuidv4 } = require("uuid");


// routes setting
app.get('/m/:message', async (req, res) => {
    const arr = []

    for (let i=0; i<3000; i++) {
        arr.push(client.sampleCall({userId: 1, message: req.params.message}, (err, data) => {
            if (err) throw err;

        }))

    }
    resolveAll(arr, 'grpc')

    for (let i=0; i<3000; i++) {
        arr.push(axios.get('http://localhost:8880/hi/sang').catch((e) => {
            throw e
        }))
    }

    resolveAll(arr, 'REST')

    res.send('Hello World')
})

app.get('/server-side/:message', async (req, res) => {
    const { message } = req.params
    const call = client.sampleServerStream({userId: 1, message}, (err, data) => {
        if (err) throw err;
    })

    call.on('data',function(res){
        console.log(res.message);
    });

    call.on('end',function(){
        console.log('closing');
    });

    res.send('server side')
})

app.get('/client-side/:message', async (req, res) => {
    const { message } = req.params
    let call = client.sampleClientStream( (err, res) => {
        if (err) throw err;
        console.log("successfully streamed from client");
        console.log("message from server: ", res.message);
    });

    let messages = ["message1", "message2", "message3"];
    for (let i=0; i<messages.length; ++i) {
        call.write({ userId: i, message:messages[i] });
        await delay(1_000)
    }

    call.end();

    res.send('client side')
})

app.get('/bi/', async (req, res) => {
    let call = client.sampleBidirectionalStream();

    call.on('data',function(res){
        console.log(`received from server: ${res.message}`);
    });

    call.on('end',function(){
        console.log('closing from client');
    });

    let messages = ["message1", "message2", "message3"];

    for (let i=0; i<messages.length; ++i) {
        await delay(2_000)
        console.log('client streaming', `${i} ${messages[i]}`)
        call.write({ userId: i, message: `${i} ${messages[i]}` });
    }


    call.end();
    res.send('bidirect stream')
})

async function resolveAll(arr, name) {
    const start = new Date();
    try {
        await Promise.all(arr)
    } catch(e) {
        console.error('error handling ', name)
        console.error(e)
    }
    const end = new Date();
    console.log(`${name} took ${(end-start) / 1000}s`)
}

async function delay(time) {
    return new Promise(resolve => setTimeout(resolve, time));
}

// start and listen on the Express server
app.listen(port, () => {
    console.log(`Express is listening on localhost:${port}`)
})
