package io.spoonshift.polly.s3;

import static com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import io.spoonshift.polly.service.PollyService;

public class S3EventProcessor {

	private AmazonS3 s3Client;
	private PollyService pollyService;
	private S3FileUploader s3FileUploader;

	public S3EventProcessor(PollyService pollyService, S3FileUploader s3FileUploader) {
		this.s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
		this.pollyService = pollyService;
		this.s3FileUploader = new S3FileUploader();
	}

	public void processS3Event(S3Event s3Event, LambdaLogger logger) {
		List<S3EventNotificationRecord> records = s3Event.getRecords();
		logger.log("Number of records in the S3Event =  " + records.size() + "\n");
		logger.log(s3Event.toJson());

		// Process S3Record
		S3EventNotificationRecord s3Record = records.get(0);
		String bucketName = s3Record.getS3().getBucket().getName();
		String keyToFetch = s3Record.getS3().getObject().getKey();

		// Synthesize the text using Polly and upload to the same bucket with the same key and .mp3 format
		s3FileUploader.uploadConvertedAudio(s3RecordConverter(bucketName, keyToFetch), keyToFetch.substring(0, keyToFetch.length() - 4), bucketName);
	}

	private SynthesizeSpeechResult s3RecordConverter(String bucketName, String keyToFetch) {
		S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, keyToFetch));
		InputStream objectData = object.getObjectContent();

		StringBuilder builder = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(objectData))) {
			String line;
			while((line = reader.readLine()) != null) {
				builder.append(line);
			}
		} catch(IOException ioe) {
			System.err.println("Exception reading the file text.");

		}

		return pollyService.convertToAudio(builder.toString());
	}
}
