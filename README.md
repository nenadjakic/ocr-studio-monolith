# ocr-studio
**Development in progress**

## Table of Contents
- [Description](#description)
- [Features](#features)
- [Technologies used](#technologies-used)
- [Getting started](#getting-started)
  - [Requirements](#requirements)
  - [Build and run](#build-and-run)
  - [Dockerize Application](#dockerize-application)
    - [Installing Docker and Docker Compose](#installing-docker-and-docker-compose)
    - [Running Application with Docker Compose](#running-application-with-docker-compose)
- [Configuration](#configuration)
  - [MongoDB Configuration](#mongodb-configuration)
  - [File Upload Configuration](#file-upload-configuration)
  - [OCR Configuration](#ocr-configuration)
- [Usage](#usage)
  - [Important REST methods](#important-rest-methods)
    - [Task controller](#task-controller)
    - [Ocr controller](#ocr-controller)
  - [Use cases](#use-cases)
  - [Licence](#license)

## Description

This application is designed for managing OCR (Optical Character Recognition) tasks. It allows users to define, schedule, and execute OCR tasks through a REST API. The core technologies used are Spring Framework, MongoDB, and Tesseract OCR.

## Features

- **Task Definition**: Users can create OCR tasks by specifying parameters such as the language, scheduling time, and other options.
- **Document Upload**: Upload the documents that need to be processed as part of the OCR task.
- **Task Scheduling**: Once the task is fully defined, it can be scheduled to run at a specified time.
- **Task Execution**: The application automatically executes the OCR tasks at the scheduled time.
- **Data Storage**:
    - **Tasks**: All task definitions and metadata are stored in a MongoDB database.
    - **Documents**: Uploaded documents are stored on the file system.

## Technologies used

- **Spring Framework**: The backbone of the application, used for dependency injection, REST API creation, and overall application management.
- **MongoDB**: A NoSQL database used to store task definitions and execution logs.
- **Tesseract OCR**: An open-source OCR engine used for processing the uploaded documents according to the defined task parameters.

## Getting Started

### Requirements
- **Java 21** or higher
- **Gradle** for dependency management
- **MongoDB** instance
- **Tesseract OCR** installed and configured
- **File System** with sufficient storage for document processing

### Build and run

To run this application locally:

1. Clone the repository.

``` git clone https://github.com/nenadjakic/ocr-studio.git ```

2. Navigate to the project directory:

``` cd ocr-studio ```

3. Configure the MongoDB connection and file storage paths in the `application.properties` file.
4. Build the project using Gradle.

``` .\gradlew clean bootJar ```

5. Run the application.  

```  .\gradlew bootRun ```

### Dockerize Application

Follow these steps to dockerize and run your application using Docker and Docker Compose:

#### Installing Docker and Docker Compose

Before dockerizing your application, ensure that Docker and Docker Compose are installed on your machine. If not already installed, you can follow these guides:

- [Docker Install Guide](https://docs.docker.com/get-docker/)
- [Docker Compose Install Guide](https://docs.docker.com/compose/install/)

#### Running Application with Docker Compose

Once Docker and Docker Compose are installed, you can use Docker Compose to build and run your application in a container:

``` docker compose up ```

## Configuration

The application can be configured using the following properties, which can be set in the `application.properties` file. These properties allow you to customize the behavior of the application, including database connections, file upload limits, logging levels, and OCR paths.

### MongoDB Configuration

- **`spring.data.mongodb.uri`**: The URI for connecting to the MongoDB database.  
  **Example:** `mongodb://localhost:2010/ocr-studio`

- **`spring.data.mongodb.uuid-representation`**: Defines how UUIDs should be represented in MongoDB. The `standard` value ensures compatibility with most systems.  
  **Default:** `standard`

### File Upload Configuration

- **`spring.servlet.multipart.max-file-size`**: Specifies the maximum file size for uploads.  
  **Default:** `1024MB`

- **`spring.servlet.multipart.max-request-size`**: Specifies the maximum request size, including the file size and any other request data.  
  **Default:** `1024MB`

### OCR Configuration

- **`ocr.task-path`**: Specifies the path where OCR tasks are stored or accessed. This path should be configured according to your file storage setup. If not specified tasks directories will be created in working app directory.   
  **Default:**  

- **`ocr.tesseract.data-path`**: Path to the Tesseract OCR data files, which include language models and other resources. Ensure that this path is correctly set to avoid errors during OCR processing.  
  **Example:** `\\\\wsl.localhost\\Ubuntu-22.04\\usr\\share\\tesseract-ocr\\4.00\\tessdata`

## Usage

This application is designed to manage OCR (Optical Character Recognition) tasks efficiently. It provides a REST API for defining, scheduling, and executing OCR tasks. Below is a guide on how to use the key features of the application.

### Important REST methods

#### Task controller
>  **Retrieve All Tasks**
> 
> To get a list of all the tasks you have, use the following endpoint:
> 
> **Endpoint**  
> `GET /task`
> 
> **Description**  
> Retrieves all tasks from the system.

> **Create a New Task**
> 
> To add a new task, use the following endpoint. You can provide details about the task and upload any necessary files.
> 
> **Endpoint**  
> `POST /task`
> 
> **Description**  
> Creates a new task based on the provided model. The request should include details of the task and any files you want to upload.
> Application will save task to database, create task's directories. Main directory will be named as task id. Parent of main direcotry will be specifed with property `ocr.task-path`
> 
> ***Created directory tree:***
> - `task_id`
>   - `input`
>   - `output`
>
> If files are provided, they will be stored into directory `task_id/input/`

> **Create a Draft Task**
>
> To create a draft of a task, use the following endpoint. This is useful for setting up tasks that are not yet finalized.
>
> **Endpoint**  
> `POST /task/draft`
>
> **Description**  
> Creates a new draft task based on the provided model. This draft can be reviewed and updated before finalizing.
>
> ***See `POST/task` for created directories.***

> **Upload Files to a Task**
>
> **Endpoint**  
> `PUT /task/upload/{id}`
>
> **Description**  
> Uploads files for the specified task, defined with `{id}`. This endpoint helps in attaching files.

### Ocr controller

> **Get OCR Job Progress**
> 
> **Endpoint**  
> `GET /ocr`
> 
> **Description**  
> Retrieves the progress of an OCR job for the specified ID. This endpoint allows you to monitor how far along the OCR process is.
> If task is not scheduled yet, application will return progress information from database.

> **Interrupt OCR Job**
> 
> **Endpoint**  
> `PUT /ocr`
> 
> **Description**  
> Interrupts the OCR job for the specified task id. Use this endpoint to stop an ongoing OCR process.

> ##### Schedule OCR Job
> 
> **Endpoint**  
> `POST /ocr`
> 
> **Description**  
> Schedules an OCR job for the specified task. If the task has a defined start time, the OCR job will begin as soon as possible after that time and when an executor is available. If no start time is defined, the job will be initiated as soon as possible depending on the availability of an executor.
> If task is already scheduled, or if it has wrong status, application will throw error.

## Use cases
- **User wants to OCR one image with some english text and as output wants pdf document. User wants to do OCR immediately.**
- **User wants to OCR multiple images. User wants to do OCR immediately.**
- **User wants to OCR multipage pdf document, and as output wants pdf document. User wants to start execution at specified time.**

## License
This project is licensed under the Apache License - see the LICENSE file for details.