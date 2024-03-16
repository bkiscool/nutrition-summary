package me.brandonkey.nutritionsummaryai;

import java.time.Duration;
import java.util.Arrays;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

public class AI {
	
	private static OpenAiService service;

	public AI(final String token)
	{
		service = new OpenAiService(token, Duration.ofSeconds(30));
		
		Logger.LOG.print("OpenAI API is ready.");
		
	}
	
	/**
	 * Have the AI generate a response from a given prompt
	 * 
	 * @param prompt The thing to ask the AI
	 * @param username The name of the user, can be null
	 * @return The AI's response
	 */
	public static String askAi(final String prompt, final String username)
	{
		Logger.LOG.print(username + " prompted the AI with \"" + prompt + "\".");
		
		final ChatCompletionRequest request = ChatCompletionRequest.builder()
													.model("gpt-3.5-turbo")
													.messages(Arrays.asList(new ChatMessage("user", prompt, username)))
													.n(1)
													.build();
		
		final ChatMessage reply = service.createChatCompletion(request).getChoices().get(0).getMessage();
		final String response = reply.getContent();
		
		Logger.LOG.print("AI responded with \"" + response + "\".");
		
		return response;
		
	}
	
	public static void shutdown()
	{
		Logger.LOG.print("Shutting down the OpenAI API.");
		service.shutdownExecutor();
		Logger.LOG.print("API is shutdown.");
		
	}

}
