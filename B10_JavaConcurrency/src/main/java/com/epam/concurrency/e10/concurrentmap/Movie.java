package com.epam.concurrency.e10.concurrentmap;

import java.util.HashSet;
import java.util.Set;

public class Movie {

	private String title;
	private int releaseYear;
	private Set<Actor> actors = new HashSet<>();

	public Movie(String title, int releaseYear) {
		this.title = title;
		this.releaseYear = releaseYear;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getReleaseYear() {
		return releaseYear;
	}

	public void setReleaseYear(int releaseYear) {
		this.releaseYear = releaseYear;
	}

	public void addActor(Actor actor) {
		actors.add(actor);
	}

	public Set<Actor> getActors() {
		return actors;
	}

	@Override
	public String toString() {
		return "Movie [title=" + title + ", releaseYear=" + releaseYear + ", actors=" + actors + "]";
	}
}
