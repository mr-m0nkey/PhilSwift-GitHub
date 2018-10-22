package us.infinitydev.ps.PhilSwift;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.json.JSONObject;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;

public class Main {
	
	final static ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	
	public static void sendChuckNorrisJoke(TextChannel c) throws Exception{
		
		c.sendMessage("Fetching Chuck Norris -> Phil Swift Joke...");
		
		//Builds Joke Request
		HttpClient client = HttpClientBuilder.create().build();
		String url = "http://api.icndb.com/jokes/random?exclude=[explicit]?";
		HttpResponse response = null;
		HttpGet getMethod = new HttpGet(url);
		
		try {
			response = client.execute(getMethod);
			
			//Decode JSON
			String result = EntityUtils.toString(response.getEntity());
			JSONObject ja = new JSONObject(result);
			JSONObject jo = ja.getJSONObject("value");
			String jokeprimal = jo.getString("joke");
			
			//Making it funny. Sometimes these replacement rules end up messing up the joke text depending on the joke
			String jokes1 = jokeprimal.replaceAll("&quot;", "\"");
			String jokes2 = jokes1.replaceAll("Chuck", "Phil");
			String jokes3 = jokes2.replaceAll("Norris", "Swift");
			String jokes4 = jokes3.replaceAll("Swift'", "Swift's");
			c.sendMessage(jokes4);
			
		}catch (Exception e) {
			c.sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
			c.sendMessage(e.getMessage());
		}
	}
	
	public static void sendShowerThought(TextChannel c, RedditClient reddit) throws Exception{
		c.sendMessage("Fetching shower thought...");
		
		DefaultPaginator<Submission> paginator = reddit.subreddit("Showerthoughts").posts().build();
		List<Listing<Submission>> firstThreePages = paginator.accumulate(7);
		
		Random rand = new Random();
		int num = rand.nextInt(firstThreePages.size() - 1) + 1;
		int numtwo = rand.nextInt(firstThreePages.get(num).size() - 1) + 1;
		
		if(firstThreePages.get(num).get(numtwo).isNsfw()) {
			sendShowerThought(c, reddit);
			return;
		}
		
		EmbedBuilder embed = new EmbedBuilder()
				.setAuthor(firstThreePages.get(num).get(numtwo).getTitle(),
						firstThreePages.get(num).get(numtwo).getUrl(),
						"https://styles.redditmedia.com/t5_2szyo/styles/communityIcon_z7dkyeif8kzz.png");
		
		c.sendMessage(embed);
	}
	
	public static void sendDadJoke(TextChannel c, RedditClient reddit) throws Exception{
		c.sendMessage("Fetching dad joke...");
		
		DefaultPaginator<Submission> paginator = reddit.subreddit("dadjokes").posts().build();
		List<Listing<Submission>> firstThreePages = paginator.accumulate(7);
		
		Random rand = new Random();
		int num = rand.nextInt(firstThreePages.size() - 1) + 1;
		int numtwo = rand.nextInt(firstThreePages.get(num).size() - 1) + 1;
		
		if(firstThreePages.get(num).get(numtwo).isNsfw()) {
			sendDankMeme(c, reddit);
			return;
		}
		
		EmbedBuilder embed = new EmbedBuilder()
				.setAuthor(firstThreePages.get(num).get(numtwo).getTitle(), 
						firstThreePages.get(num).get(numtwo).getUrl(), 
						"https://styles.redditmedia.com/t5_2t0no/styles/communityIcon_hgh0b0qcc3w01.png")
				.setDescription(firstThreePages.get(num).get(numtwo).getSelfText());
		c.sendMessage(embed);
	}
	
	public static void sendDeepThought(TextChannel c, RedditClient reddit) throws Exception{
		c.sendMessage("Fetching \"I'm 14 and this is deep\" post...");
		
		DefaultPaginator<Submission> paginator = reddit.subreddit("im14andthisisdeep").posts().build();
		List<Listing<Submission>> firstThreePages = paginator.accumulate(7);
		
		Random rand = new Random();
		int num = rand.nextInt(firstThreePages.size() - 1) + 1;
		int numtwo = rand.nextInt(firstThreePages.get(num).size() - 1) + 1;
		
		if(firstThreePages.get(num).get(numtwo).isNsfw()) {
			sendDankMeme(c, reddit);
			return;
		}
		
		URL url;
		try {
			url = new URL(firstThreePages.get(num).get(numtwo).getUrl());
			try {
				String format;
				if(firstThreePages.get(num).get(numtwo).getUrl().contains(".png")) {
					format = "png";
				}else if(firstThreePages.get(num).get(numtwo).getUrl().contains(".jpg")) {
					format = "jpg";
				}else {
					format = "png";
				}
				
				BufferedImage img = ImageIO.read(url);
				File file = new File(firstThreePages.get(num).get(numtwo).getId() + "." + format);
				ImageIO.write(img, format, file);
				EmbedBuilder embed = new EmbedBuilder()
						.setAuthor(firstThreePages.get(num).get(numtwo).getTitle(), 
								"https://www.reddit.com/r/im14andthisisdeeps/comments/" + firstThreePages.get(num).get(numtwo).getId() + "/", 
								"https://b.thumbs.redditmedia.com/m05MkTXkB1_m_-W6uHcpq6Z34061E8BNCLJgSoZuweg.png")
						.setImage(file);
				c.sendMessage(embed);
				Thread.sleep(500);
				if(file.delete()) {
					System.out.println(file.getName() + " deleted successfully");
				}else {
					System.out.println("Issue deleting " + file.getName());
				}
			} catch (IOException e) {
				c.sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
				c.sendMessage(e.getMessage());
			}
			
		} catch (MalformedURLException e) {
			c.sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
			c.sendMessage(e.getMessage());
		}
		
	}
	
	public static void sendDankMeme(TextChannel c, RedditClient reddit) throws Exception{
		c.sendMessage("Fetching dank meme...");
		
		DefaultPaginator<Submission> paginator = reddit.subreddit("dankmemes").posts().build();
		List<Listing<Submission>> firstThreePages = paginator.accumulate(7);
		
		Random rand = new Random();
		int num = rand.nextInt(firstThreePages.size() - 1) + 1;
		int numtwo = rand.nextInt(firstThreePages.get(num).size() - 1) + 1;
		
		if(firstThreePages.get(num).get(numtwo).isNsfw()) {
			sendDankMeme(c, reddit);
			return;
		}
		
		URL url;
		try {
			url = new URL(firstThreePages.get(num).get(numtwo).getUrl());
			try {
				String format;
				if(firstThreePages.get(num).get(numtwo).getUrl().contains(".png")) {
					format = "png";
				}else if(firstThreePages.get(num).get(numtwo).getUrl().contains(".jpg")) {
					format = "jpg";
				}else {
					format = "png";
				}
				
				BufferedImage img = ImageIO.read(url);
				File file = new File(firstThreePages.get(num).get(numtwo).getId() + "." + format);
				ImageIO.write(img, format, file);
				EmbedBuilder embed = new EmbedBuilder()
						.setAuthor(firstThreePages.get(num).get(numtwo).getTitle(), 
								"https://www.reddit.com/r/dankmemes/comments/" + firstThreePages.get(num).get(numtwo).getId() + "/", 
								"https://b.thumbs.redditmedia.com/xG3ByxBd2GgreMWBFoaXgH4y9kLSjbpU-HQU-WQQpDQ.png")
						.setImage(file);
				c.sendMessage(embed);
				Thread.sleep(500);
				if(file.delete()) {
					System.out.println(file.getName() + " deleted successfully");
				}else {
					System.out.println("Issue deleting " + file.getName());
				}
			} catch (IOException e) {
				c.sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
				c.sendMessage(e.getMessage());
			}
			
		} catch (MalformedURLException e) {
			c.sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
			c.sendMessage(e.getMessage());
		}
		
	}
	
	 public static void main(String[] args) {
		
		//Setup Reader for Data Input
		Scanner reader = new Scanner(System.in);
		
		//Bot Token
		System.out.println("Enter bot token: ");
		String token = reader.nextLine();

		//Logging in to the DiscordAPI
        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();
        
        //Some required Reddit info
        UserAgent userAgent = new UserAgent("bot", "us.infinitydev.ps", "v1.0", "philswiftbot");
        
        //Reddit username
        System.out.println("Enter Reddit Username: ");
        String rUsername = reader.nextLine();
        
        //Reddit password
        System.out.println("Enter Reddit Password: ");
        String rPass = reader.nextLine();
        
        //Reddit clientID
        System.out.println("Enter Reddit ClientID: ");
        String rClientID = reader.nextLine();
        
        //Reddit Client Secret
        System.out.println("Enter Reddit ClientSecret: ");
        String rClientSecret = reader.nextLine();
        
        //RedditAPI login
        Credentials credentials = Credentials.script(rUsername, rPass, rClientID, rClientSecret);
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient reddit = OAuthHelper.automatic(adapter, credentials);

        //Status: "Playing with Flex Seal"
        api.updateActivity("with Flex Seal");
        
        //Info to let user know the bot is online
        System.out.println("PHIL SWIFT IS HERE WITH A LOT OF DAMAGE STORED UP TO UNLEASH UPON YOUR DISCORD!");
        
        //Bot URL creation
        String botInvite = api.createBotInvite().replaceAll("0", "201452544");
        System.out.println("You can invite the bot by using the following url: " + botInvite);
        
        api.addMessageCreateListener(event -> {
            
        	//Some Commercials
        	
            if(event.getMessage().getContent().equalsIgnoreCase("!flextapec")) {
            	event.getChannel().sendMessage("https://www.youtube.com/watch?v=0xzN6FM5x_E");
            }
            
            if(event.getMessage().getContent().equalsIgnoreCase("!flexsealliquidc")) {
            	event.getChannel().sendMessage("https://www.youtube.com/watch?v=_NygbA9UYPw");
            }
            
            if(event.getMessage().getContent().equalsIgnoreCase("!flextapeclearc")) {
            	event.getChannel().sendMessage("https://www.youtube.com/watch?v=NH8pM0cgz2k");
            }
            
            if(event.getMessage().getContent().equalsIgnoreCase("!flexgluec")) {
            	event.getChannel().sendMessage("https://www.youtube.com/watch?v=LOZi8H0YMiA");
            }
            
            //Chuck Norris Jokes: But Phil Swift is Chuck Norris
            
            if(event.getMessage().getContent().equalsIgnoreCase("!chucknorris")) {
            	try {
					sendChuckNorrisJoke(event.getChannel());
				} catch (Exception e) {
					event.getChannel().sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
					event.getChannel().sendMessage(e.getMessage());
				}
            }
            
            //Reddit pulls
            
            if(event.getMessage().getContent().equalsIgnoreCase("!showerthought")) {
        	    try {
				    sendShowerThought(event.getMessage().getChannel(), reddit);
			    } catch (Exception e) {
				    event.getChannel().sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
				    event.getChannel().sendMessage(e.getMessage());
			    }
            }
           
            if(event.getMessage().getContent().equalsIgnoreCase("!deepthought")) {
            	try {
            		sendDeepThought(event.getMessage().getChannel(), reddit);
            	} catch(Exception e) {
            		event.getChannel().sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
    				event.getChannel().sendMessage(e.getMessage());
            	}
            }
            
            //IDK. You can knife people I guess
            
            if(event.getMessage().getContent().split(" ")[0].equals("!knife")) {
            	if(event.getMessage().getContent().contains(" ")) {
            		String msg = event.getMessage().getContent().split(" ")[1];
                	if(msg.contains("<") && msg.contains(">") && msg.contains("@")) {
                		event.getChannel().sendMessage("*Knives " + event.getMessage().getContent().split(" ")[1] + "*");
                		event.getChannel().sendMessage("THAT's a lot of damage.");
                	}else {
                		event.getChannel().sendMessage("Knives cause A LOT of damage. But nothing that Flex Seal can't fix!");
                	}
            	}else {
            		event.getChannel().sendMessage("Knives cause A LOT of damage. But nothing that Flex Seal can't fix!");
            	}
            }
            
            //Starting a message with "I'm" makes Phil do some annoying echo-ing
            
            if(event.getMessage().getContent().split(" ")[0].equalsIgnoreCase("I'm")) {
            	if(event.getMessage().getContent().contains(" ")) {
            		int i = 0;
            		String repeat = "";
            		for(String s : event.getMessage().getContent().split(" ")) {
            			if(i == 0) {
            				i++;
            			}else if(i == event.getMessage().getContent().split(" ").length - 1) {
            				repeat = repeat + s;
            			}else {
            				repeat = repeat + s + " ";
            			}
            		}
            		event.getMessage().getChannel().sendMessage("Hi, " + repeat + ", I'm Phil Swift!");
            	}
            }
            
            //MORE reddit pulls
            
            if(event.getMessage().getContent().equalsIgnoreCase("!dankmeme")) {
            	try {
					sendDankMeme(event.getMessage().getChannel(), reddit);
				} catch (Exception e) {
					event.getChannel().sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
					event.getChannel().sendMessage(e.getMessage());
				}
            }
            
            if(event.getMessage().getContent().equalsIgnoreCase("!dadjoke")) {
            	try {
					sendDadJoke(event.getMessage().getChannel(), reddit);
				} catch (Exception e) {
					event.getChannel().sendMessage("Oh Neptune, <@185841137196597248> (SentinelGaming#4525) help me!");
					event.getChannel().sendMessage(e.getMessage());
				}
            }
            
            //Anti-suicide easter egg
            
            if(event.getMessage().getContent().contains("I'm going to commit suicide")) {
            	event.getChannel().sendMessage("Hey pal. Even Flex Seal can't patch that up. Call +1 (800) 273-8255.");
            }
            
            if(event.getMessage().getContent().contains("I want to die")) {
            	event.getChannel().sendMessage("Hey pal. Even Flex Seal can't patch that up. Call +1 (800) 273-8255.");
            }
            
            if(event.getMessage().getContent().equalsIgnoreCase("!help")) {
            	event.getChannel().sendMessage("Hey, Phil Swift here. I'm hitting you up in the DMs ( ͡° ͜ʖ ͡°)");
            	event.getMessage().getAuthor().asUser().get().sendMessage(
            			"Commercials/Official Phil Swift Content: !flextapec, !flexsealliquidc, !flextapeclearc, and !flexgluec\n"
            			+ "Jokes: !chucknorris, and !knife <tag>\n"
            			+ "Reddit: !showerthought, !dankmeme, !dadjoke, and !deepthought\n");
            }
            
        });
        
        //Join and leave server notifications
        
        api.addServerJoinListener(event -> System.out.println("Joined server " + event.getServer().getName()));
        api.addServerLeaveListener(event -> System.out.println("Left server " + event.getServer().getName()));
        
	}
	
}
