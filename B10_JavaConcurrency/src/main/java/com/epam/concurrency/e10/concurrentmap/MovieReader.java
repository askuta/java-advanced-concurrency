package com.epam.concurrency.e10.concurrentmap;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieReader {

	public void addActorsToMap(Map<Actor, Set<Movie>> map) {
		Set<Movie> movies = readMovies();
		for (Movie movie : movies) {
			for (Actor actor : movie.getActors()) {
				map.computeIfAbsent(actor, a -> new HashSet<>()).add(movie);
			}
		}
	}

	public Set<Movie> readMovies() {
		try (Stream<String> lines = Files.lines(Paths.get(MovieReader.class.getClassLoader().getResource("movies-mpaa.txt").toURI()), StandardCharsets.ISO_8859_1)) {
			Set<Movie> movies = lines.map((String line) -> {
				String[] elements = line.split("/");
				String title = extractTitle(elements[0]);
				String releaseYear = extractReleaseYear(elements[0]);

				Movie movie = new Movie(title, Integer.valueOf(releaseYear));

				Arrays.stream(elements)
						.skip(1)
						.map(MovieReader::extractActor)
						.forEach(movie::addActor);

				return movie;
			}).collect(Collectors.toSet());

			return movies;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	private static Actor extractActor(String elements) {
		String[] nameElements = elements.split(", ");
		String firstName = extractFirstName(nameElements);
		String lastName = extractLastName(nameElements);

		return new Actor(lastName, firstName);
	}

	private static String extractFirstName(String[] nameElements) {
		return nameElements[0];
	}

	private static String extractLastName(String[] nameElements) {
		if (nameElements.length == 2) {
			return nameElements[1];
		}

		return "";		
	}

	private static String extractTitle(String element) {
		return element.split("\\(")[0].trim();
	}

	private static String extractReleaseYear(String element) {
		String releaseYear = element.split("\\(")[1].split("\\)")[0];
		return releaseYear.matches("\\d+") ? releaseYear : releaseYear.split(",")[0];
	}
}
