package com.jtwitweb.controller;

import java.util.HashMap;
import java.util.Iterator;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.jtwitweb.entities.Pinholder;
import com.jtwitweb.entities.User;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

@Controller
@RequestMapping(JTwitController.HOME_URL)
@SessionAttributes({ "user" })
public class JTwitController {

	ConfigurationBuilder cb;
	TwitterFactory tf;
	HashMap<Long, Twitter> connections;
	Twitter twitter;
	RequestToken requestToken = null;
	AccessToken accessToken = null;

	public static final String HOME_URL = "/";

	public JTwitController() {
		connections = new HashMap<Long, Twitter>();
	}

	@RequestMapping(method = RequestMethod.GET)
	public String loginTwitter(Model model) {

		try {
			cb = new ConfigurationBuilder().setOAuthConsumerKey("ISxkuLISWGN83O7babl0pvnPO")
					.setOAuthConsumerSecret("pHtdY68fxah73OnZyvRa8GVYKqlFON3nsMrQJ9Ou1XBNrH8eIi");
			tf = new TwitterFactory(cb.build());
			twitter = tf.getInstance();
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		model.addAttribute("auth", requestToken.getAuthorizationURL());
		model.addAttribute("pinholder", new Pinholder());
		return "index";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String MainScreen(@ModelAttribute Pinholder pinholder, ModelMap modelmap) {
		Boolean loginFailure = false;
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pinholder.getPin());
			twitter.setOAuthAccessToken(accessToken);
			User user = new User();
			user.setUserID(twitter.getId());
			modelmap.addAttribute(user);
			user.setUserProfile(twitter.showUser(twitter.getId()));
			connections.put(user.getUserID(), twitter);
		} catch (TwitterException e) {
			loginFailure = true;
		}
		modelmap.addAttribute("loginFailure", loginFailure);
		return "twitter/loginConfirm";
	}

	@RequestMapping(value = "tweets",method=RequestMethod.GET)
	public String getTweets(@ModelAttribute User user, ModelMap modelmap) {
		Twitter userTwitter = connections.get(user.getUserID());
		ResponseList<Status> userTimeline = null;
		ResponseList<Status> homeTimeline = null;
		try {
			userTimeline = userTwitter.getUserTimeline();
			homeTimeline = userTwitter.getHomeTimeline();
			user.userTimeline = userTimeline;
			user.homeTimeline = homeTimeline;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return "twitter/home";
	}
	
	@RequestMapping(value = "tweets",method=RequestMethod.POST)
	public String postTweet(@ModelAttribute User user, ModelMap modelmap,@RequestHeader String tweetData) {
		Twitter userTwitter = connections.get(user.getUserID());
		ResponseList<Status> userTimeline = null;
		ResponseList<Status> homeTimeline = null;
		try {
			userTwitter.updateStatus(tweetData);
			userTimeline = userTwitter.getUserTimeline();
			homeTimeline = userTwitter.getHomeTimeline();
			user.userTimeline = userTimeline;
			user.homeTimeline = homeTimeline;
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return "twitter/home";
	}

	@RequestMapping(value = "tweets/like/{likeID}")
	public String makeLike(@ModelAttribute User user, ModelMap modelmap, @PathVariable String likeID) {
		Twitter userTwitter = connections.get(user.getUserID());
		try {
			Long id = Long.parseLong(likeID);
			Status newStatus = userTwitter.createFavorite(id);
			updateFeeds(user, id, newStatus);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return "twitter/home";
	}

	@RequestMapping(value = "tweets/like/{likeID}",method = RequestMethod.DELETE)
	public String removeLike(@ModelAttribute User user, ModelMap modelmap, @PathVariable String likeID) {
		Twitter userTwitter = connections.get(user.getUserID());
		try {
			Long id = Long.parseLong(likeID);
			Status newStatus = userTwitter.destroyFavorite(id);
			updateFeeds(user, id, newStatus);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return "twitter/home";
	}
	
	@RequestMapping(value = "tweets/retweet/{retweetID}",method = RequestMethod.GET)
	public String retweet(@ModelAttribute User user, ModelMap modelmap, @PathVariable String retweetID) {
		Twitter userTwitter = connections.get(user.getUserID());
		try {
			Long id = Long.parseLong(retweetID);
			Status tweet=userTwitter.showStatus(id);
			Status newStatus = userTwitter.retweetStatus(id);
			if(tweet.isRetweet()){
				updateFeeds(user, id, newStatus);
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return "twitter/home";
	}
	
	@RequestMapping(value = "tweets/delete/{deleteID}",method = RequestMethod.DELETE)
	public String delTweet(@ModelAttribute User user, ModelMap modelmap, @PathVariable String deleteID) {
		Twitter userTwitter = connections.get(user.getUserID());
		try {
			Long id = Long.parseLong(deleteID);
			userTwitter.destroyStatus(id);
			updateFeeds(user, id, null);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return "twitter/home";
	}
	
	public void updateFeeds(User user,Long id,Status newStatus) {
		boolean found = false;
		for (Iterator<Status> iterator = user.getHomeTimeline().iterator(); iterator.hasNext();) {
			Status status = (Status) iterator.next();
			if (status.getId() == id) {
				status = newStatus;
				found = true;
				break;
			}
		}
		if (!found) {
			for (Iterator<Status> iterator = user.getHomeTimeline().iterator(); iterator.hasNext();) {
				Status status = (Status) iterator.next();
				if (status.getId() == id) {
					status = newStatus;
					found = true;
					break;
				}
			}
		}
	}
}
