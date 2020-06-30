package com.epam.concurrency.e10.concurrentmap;

import java.util.Objects;

public class Actor {

	private String firstName;
	private String lastName;

	public Actor(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(lastName);
		hash = 67 * hash + Objects.hashCode(firstName);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Actor other = (Actor) obj;
		if (!Objects.equals(this.lastName, other.lastName)) {
			return false;
		}
		return Objects.equals(this.firstName, other.firstName);
	}

	@Override
	public String toString() {
		return "Actor [firstName=" + firstName + ", lastName=" + lastName + "]";
	}
}
