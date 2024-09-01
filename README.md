# ocr-studio
**Development in progress**
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

## Technologies Used

- **Spring Framework**: The backbone of the application, used for dependency injection, REST API creation, and overall application management.
- **MongoDB**: A NoSQL database used to store task definitions and execution logs.
- **Tesseract OCR**: An open-source OCR engine used for processing the uploaded documents according to the defined task parameters.

## How to Use

1. **Define a Task**: Use the REST API to define an OCR task. Parameters include language, execution time, and Tesseract options.
2. **Upload Documents**: Attach the documents that need to be processed by the OCR task. The documents will be saved to the file system.
3. **Schedule the Task**: Once all necessary details are provided, schedule the task to run at the desired time.
4. **Execution**: The application will execute the task at the scheduled time, processing the documents with the specified Tesseract options.

## Getting Started

To run this application locally:

1. Clone the repository.
``` bash 
git clone https://github.com/nenadjakic/ocr-studio.git
cd ocr-studio
```
2. Configure the MongoDB connection and file storage paths in the `application.properties` file.
3. Build the project using Gradle.
```bash 
.\gradlew clean bootJar
```
4. Run the application.  
``` bash 
.\gradlew bootRun 
```

## Requirements

- **Java 21** or higher
- **Gradle** for dependency management
- **MongoDB** instance
- **Tesseract OCR** installed and configured
- **File System** with sufficient storage for document processing

## License
This project is licensed under the Apache License - see the LICENSE file for details.