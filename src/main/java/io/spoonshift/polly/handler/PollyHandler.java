package io.spoonshift.polly.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.VoiceId;
import io.spoonshift.polly.s3.S3EventProcessor;
import io.spoonshift.polly.s3.S3FileUploader;
import io.spoonshift.polly.service.PollyService;

public class PollyHandler {

	private S3EventProcessor s3EventProcessor;
	private PollyService pollyService;
	private S3FileUploader fileUploader;

	public PollyHandler() {
		pollyService = new PollyService(OutputFormat.Mp3, VoiceId.Amy);
		fileUploader = new S3FileUploader();
		s3EventProcessor = new S3EventProcessor(pollyService, fileUploader);
	}

	public void handleRequest(S3Event s3Event, Context context) {
		LambdaLogger logger = context.getLogger();

		s3EventProcessor.processS3Event(s3Event, logger);
	}
}
