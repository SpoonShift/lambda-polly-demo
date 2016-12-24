package io.spoonshift.polly.s3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3FileUploader {
	private static final String FILE_FORMAT = ".mp3";

	private AmazonS3 s3Client;

	public S3FileUploader() {
		this.s3Client = new AmazonS3Client(new DefaultAWSCredentialsProviderChain());
	}

	public void uploadConvertedAudio(SynthesizeSpeechResult speechResult, String key, String bucketName) {
		Path temp = null;
		try {
			temp = Files.createTempFile(key, FILE_FORMAT);
			FileUtils.copyInputStreamToFile(speechResult.getAudioStream(), temp.toFile());
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, temp.toFile());
			s3Client.putObject(putObjectRequest);
		} catch(IOException ioe) {
			System.err.println("Exception while uploading the converted audio file.");
		} finally {
			try {
				if (Files.exists(temp)) {
					Files.delete(temp);
				}
			} catch (IOException ioe) {
				System.err.println("Cannot delete file.");
			}
		}
	}
}
