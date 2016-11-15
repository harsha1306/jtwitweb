function showUserTweets() {
	document.getElementById("userTweets").style.display = 'block';
	document.getElementById("timeLine").style.display = 'none';
}

function showTimeline() {
	document.getElementById("userTweets").style.display = 'none';
	document.getElementById("timeLine").style.display = 'block';
}

function tweetDialog() {
	document.getElementById("tweetWindow").style.display = 'block';
}

function cancelTweet() {
	document.getElementById("myTweet").value = '';
	document.getElementById("tweetWindow").style.display = 'none';
}

function sendTweet() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			document.getElementById("myTweet").value = '';
			document.getElementById("tweetWindow").style.display = 'none';
			location.reload();
		}
	};
	xhttp.open("POST", "/tweets/", true);
	var tweetData = document.getElementById("myTweet").value;
	if (tweetData.length > 140) {
		alert("Tweet should be less than 140 characters");
	} else {
		xhttp.setRequestHeader("tweetData", tweetData);
		xhttp.send();
	}

}

function retweet(id, button) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			button.className = "retweeted";
			button.onclick = function() {
			};
		}
	};
	xhttp.open("GET", "/tweets/retweet/" + id, true);
	xhttp.send();
}

function like(id, button) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			button.className = "liked";
			button.onclick = function() {
				dislike(id, button);
			};
		}
	};
	xhttp.open("GET", "/tweets/like/" + id, true);
	xhttp.send();
}

function dislike(id, button) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			button.className = "like";
			button.onclick = function() {
				like(id, button);
			};
		}
	};
	xhttp.open("DELETE", "/tweets/like/" + id, true);
	xhttp.send();
}

function deleteTweet(id, button) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			button.parentElement.parentElement.parentElement.style.display = 'none';
		}
	};
	xhttp.open("DELETE", "/tweets/delete/" + id, true);
	xhttp.send();
}

function refresh() {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			location.reload();
		}
	};
	xhttp.open("GET", "/tweets/", true);
	xhttp.send();
}

function openExternal(element){
	var win = window.open(element.getAttribute("href"), '_blank');
	  win.focus();
}

function countChars(tweetText,displayCounter){
	var len = document.getElementById(tweetText).value.length;
	  document.getElementById(displayCounter).innerHTML = len+"/140";
	  if(len>140){
		  document.getElementById(displayCounter).style.color="red";
	  }
	  else{
		  document.getElementById(displayCounter).style.color="inherit";
	  }
}
