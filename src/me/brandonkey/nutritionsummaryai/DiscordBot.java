package me.brandonkey.nutritionsummaryai;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class DiscordBot extends ListenerAdapter {
	
	private static JDA jda;
	private static Guild GUILD;

	public DiscordBot(final String token, final String guildId) {
		Logger.LOG.print("Starting Discord bot.");
		try {
			jda = JDABuilder.createDefault(token)
							.addEventListeners(this)
							.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT)
							.build().awaitReady();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		final CommandData summarize = Commands.message("Summarize nutrition and cost");
		
		GUILD = jda.getGuildById(guildId);
		
		jda.updateCommands().queue();
		jda.updateCommands().addCommands(summarize).queue();
		
	}
	
	@Override
	public void onMessageContextInteraction(MessageContextInteractionEvent event)
	{
		
		Logger.LOG.print("Discord user ran \"Summarize nutrition and cost\" context menu");
		event.deferReply().queue();
		
		final String askPrompt = "Concisely, count about how many calories I ate in this day, assuming 1 serving unless noted otherwise. Also concisely, about how much did all this food cost, in USD, assuming the food was bought in San Diego, California:\n" + event.getTarget().getContentRaw();
		
		new Runnable()
		{
			@Override
			public void run()
			{
				final String response = AI.askAi(askPrompt, event.getUser().getName());
				event.getHook().sendMessage(response).queue();
				
			}
			
		}.run();
		
	}
	
	@Override
	public void onReady(ReadyEvent event)
	{
		Logger.LOG.print("Discord bot is ready.");
		
	}
	
	/*
	@Override
	public void onMessageReceived(MessageReceivedEvent event)
	{
		if (event.getAuthor().isBot()) return;
		
		final String message = event.getMessage().getContentDisplay();
		
		if (message.startsWi)
		
	}
	*/
	
	private static MessageCreateAction sendMessage(final String message, final MessageChannel channel)
	{
		Logger.LOG.print("Sending message: " + message);
		return channel.sendMessage(message);
	}
	
	public static void shutdown()
	{
		Logger.LOG.print("Shutting down the Discord bot.");
		sendMessage("Shutting down..", GUILD.getDefaultChannel().asTextChannel()).complete();
		jda.shutdown();
		Logger.LOG.print("Discord bot finished shutting down.");
		
	}

}
