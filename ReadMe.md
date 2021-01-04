

## Online Machine Learning Clients

Example implementation of basic communication mechanisms with the 
Online Machine Learning component via Kafka Clients.

Interactive Extreme-Scale Analytics and Forecasting | INFORE

ATHENA 'Research & Innovation Information Technologies'


### Getting Started

The Online Machine Learning (OML) component, implemented in Flink, provides distributed Online Machine Leaning training
mechanisms on a stream of data, by utilising the Parameter Server paradigm. There are three Kafka input streams. 
Starting with the input streams, one provides a data stream of labeled and unlabeled data to train multiple Machine 
Learning Pipelines simultaneously, the second one provides a data stream of unlabeled only data to make predictions on 
them using the trained ML Pipelines of the job, and the last one for sending requests ("Create", "Update", "Query", 
"Delete") to manage the algorithms running on the component. OML also provides two output Kafka stream one for providing 
the predictions stream, and one for answering "Query" requests. Lastly, there is an internal to the OML component Kafka 
stream, with number of partitions equal to the parallelism of the job, used by the component for emulating the feedback 
of a Parameter Server paradigm. We do not provide any Kafka client for the last Kafka stream, as it does not provide any
communication mechanism to and from the OML component.

### TrainingDataStreamProducer Class
This class is a simple implementation for a kafka producer that evenly partitions training data (based on the number of 
kafka partitions) and sends the corresponding requests to the OML component via the "trainingData" topic. A Kafka topic 
needs to be created beforehand in a kafka cluster with the same number of partitions as the parallelism of the Online 
Machine Leaning component implemented in Flink.  
A simple command is the following:

```
kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 16 --replication-factor 4 --topic trainingData
```

The training data are provided in String written in JSON formats. The JSON format of a data point provides internal data 
structures for numerical as well as categorical features and optionally a label. The training data String is being 
serialized by the client and is sent to the OML. The component then deserializes the training data back into a String 
and then converts it into a DataInstance POJO class object and forwards it to the rest of the Flink workflow. 

A dummy training dataset of 800 data points is the provided as /data/trainingData.csv file, containing csv data points 
for a binary classification problem. Each line in the file represents a data point. The first 30 fields represent 
continuous numerical features, while the last field represents the true label of the observation. For the generation of 
a bigger dummy dataset, check the provided dataGen.py script. Each csv data point is converted into a String in JSON 
format by utilizing the DataInstance POJO class. Check the /src/main/java/omlClient/TrainingDataClient file.

### ForecastingDataStreamProducer Class
Similarly to the training data case, we provide a Kafka producer for sending unlabeled only data to the OML component to 
generate predictions on them. A Kafka topic needs to be created beforehand in 
a kafka cluster with the same number of partitions as the parallelism of the OML component implemented in Flink.  
A simple command is the following:

```
kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 16 --replication-factor 4 --topic forecastingData
```

Again, a dummy forecasting dataset of 100 unlabeled data points is provided as /data/forecastingData.csv. Check the 
/src/main/java/omlClient/ForecastingDataClient file.

### RequestProducer Class
This class is a simple implementation for a kafka producer that sends requests to the OML component via the "requests" 
topic. A Kafka topic needs to be created beforehand in a kafka cluster with one partition. 
A simple command is the following:

```
kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 1 --replication-factor 4 --topic requests
```

The requests are provided in String written in JSON format. The JSON String is serialized and sent to the OML. The 
component then deserializes the message back into a String and then converts it into a Request POJO class object and 
forwards it to the rest of the Flink workflow. Request types are "Create", "Update", "Query" and "Delete" to manage the 
Machine Learning Pipelines inside the component. For each json request, a unique id and a request type must be provided. 
Then, depending on the request type, you can provide metadata for a list of preprocessors a single machine learning 
method (The concatenation of multiple preprocessors with a machine learning algorithm constitutes a Machine Learning Pipeline). 
We provide example requests in the Requests.json file, that we also run for our experiments, and a second example file
Requests2.json. Only the "Create" and "Delete" and "Query" methods are implemented inside OML at the moment. The "Update" 
request is also valid and will be processed by the OML component, but it will not do anything. Use "Create" and "Delete" 
and "Query" for any testing. Check the /src/main/java/omlClient/RequestClient file.

### ResponseConsumer Class
This class is a simple implementation of a Kafka Consumer that receives responses from the OML component. These are 
responses to a user "Query" request, where the user asks for information for a specific ML pipeline from the component. 
A Kafka topic needs to be created beforehand in a kafka cluster with one partition. 
A simple command is the following:

```
kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 1 --replication-factor 4 --topic responses
```

OML sends a serialized String response, where is deserialized here, inside the client and converted into a QueryResponse
JOJO class (The last step is not necessary, it is used for better console prints). When you use the "Query" request, you
have to provide both the id of the ML pipeline that you wish to query along with a Long id for the request. Check the 
/src/main/java/omlClient/ResponseClient file.

### PredictionConsumer Class
This class is a simple implementation of a Kafka Consumer that receives predictions from the OML component. These are 
predictions to the forecasting data that were provided by the user in the "forecastingData" topic. A Kafka topic needs 
to be created beforehand in a kafka cluster with the same number of partitions as the parallelism of the OML component 
implemented in Flink. A simple command is the following:
                                        
```
kafka/bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --partitions 16 --replication-factor 4 --topic predictions
```

OML sends a serializes String prediction, where is deserialized here, inside the client. Check the 
/src/main/java/omlClient/PredictionClient file.

### Preprocessors
* **PolynomialFeatures** : hyper parameters -> { Double degree (default 2) }, parameters -> None
* **StandardScaler** : hyper parameters -> {Boolean learnable (default) true}, parameters -> {Double[] mean, 
Double[] variance, Long count (default 0)} Mean and Variance should be of size #features in the data. Default values: 
zeros

### ML algorithms
* **PA** : hyper parameters -> { Double C (default 0.01) }, parameters -> An array of doubles named "a" of size equal 
to #features in the data, and a double "b", the bias. Default value: zeros
* **regressorPA** : hyper parameters -> { Double C (default 0.01), Double epsilon (default 0.0)}, parameters -> An array 
of doubles named "a" of size equal to #features in the data, and a double "b", the bias. Default value: zeros
* **ORR** : hyper parameters -> { Double lambda (default 0.0)}, parameters -> An array of doubles of size
(#features + 1)^2 , named "A" and another array of doubles of size #features + 1, named "b". Default value: zeros

### The OML component
Before running the OML component, you should first create the following Kafla topics:
* topic: "trainingData", partitions: same as the OML Flink job
* topic: "forecastingData", partitions: same as the OML Flink job
* topic: "requests", partitions: 1
* topic: "responses", partitions: 1
* topic: "predictions", partitions: same as the OML Flink job
* topic: "psMessages", partitions: same as the OML Flink job (This topic is used by the OML component for the feedback loop)

When starting the OML job, you should provide the next arguments:
~~~
parallelism: The parallelism of the Job. DEFAULT VALUE="16"
checkpointing: Whether or not to enable the checkpointing mechanism in Flink. Use this only with high parallelism. DEFAULT VALUE="false"
trainingDataTopic: The topic name for the training data stream. DEFAULT VALUE="trainingData"
trainingDataAddr: The broker list for the "trainingData" topic. DEFAULT VALUE="localhost:9092"
forecastingDataTopic: The topic name for the forecasting data stream. DEFAULT VALUE="forecastingData"
forecastingDataAddr: The broker list for the "forecastingData" topic. DEFAULT VALUE="localhost:9092"
requestsTopic: The topic name for the requests data stream. DEFAULT VALUE="requestsData"
requestsAddr: The broker list for the "requests" Kafka topic. DEFAULT VALUE="localhost:9092"
predictionsTopic: The topic name for the predictions data stream. DEFAULT VALUE="predictionsData"
predictionsAddr: The broker list for the "predictions" Kafka topic. DEFAULT VALUE="localhost:9092"
responsesTopic: The topic name for the responses data stream. DEFAULT VALUE="responsesData"
responsesAddr: The broker list for the "responses" Kafka topic. DEFAULT VALUE="localhost:9092"
psMessagesTopic: The topic name for the parameter server messages data stream. DEFAULT VALUE="psMessagesData"
psMessagesAddr: The broker list for the "psMessages" Kafka topic. DEFAULT VALUE="localhost:9092"
~~~

### Stable tests
I recommend running a job with the ML Pipelines written in Requests.json file, meaning, use a simple PA classifier with
a Polynomial feature transformer. You could also test sending a Delete message as well as the Query. Do not hesitate to
send me an e-mail for any questions.


### TWO IMPORTANT ADDITIONS ###
1) The OML component is not a service, meaning it cannot run on multiple streams. In the trainingData topic you have to 
provide data that are originated from the same problem, but never the less you can train multiple algorithms 
(ML Pipelines) for that specific problem at the same time, as well us configure any of them on runtime.

2) The training and the forecasting procedure are in nature somewhat disconnected. Training and forecasting are both 
done online. The forecasting part will provide predictions as long as it contains models. If you create an ML Pipeline
to train via the request stream, an identical Pipeline will be created in the Forecasting workflow as well, and will be 
kept up to date will the training model. The forecasting workflow does not need to wait for the training procedure to 
"end" or "finish" in some way. It just keeps itself informed while training happens. Later on (after this deliverable), 
the OML functionality will be extended to offer the user the ability to upload a pre-trained model only in the 
forecasting part to provide online predictions without training it. This could be handy in cases where users have 
already in their possession some pre-trained models stored somewhere and would like to use them for online predictions 
without changing them, or training them further.

## Author
* **Konidaris Vissarion** v.b.konidaris@gmail.com