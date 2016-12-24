package io.spoonshift.polly.service;

import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.VoiceId;

public class PollyService {

	private AmazonPollyClient amazonPollyClient;
	private OutputFormat outputFormat;
	private VoiceId voiceId;

	public PollyService(OutputFormat outputFormat, VoiceId voiceId) {
		this.amazonPollyClient = new AmazonPollyClient();
		this.outputFormat = outputFormat;
		this.voiceId = voiceId;
	}

	public SynthesizeSpeechResult convertToAudio(String textToConvert) {
		SynthesizeSpeechRequest speechRequest = new SynthesizeSpeechRequest();
		speechRequest.withOutputFormat(outputFormat)
				.withText(textToConvert)
				.withVoiceId(voiceId);

		SynthesizeSpeechResult speechResult = amazonPollyClient.synthesizeSpeech(speechRequest);

		return speechResult;
	}
}
