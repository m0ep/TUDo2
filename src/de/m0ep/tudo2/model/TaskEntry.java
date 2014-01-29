package de.m0ep.tudo2.model;

import java.io.Serializable;

public class TaskEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private short state;
	private short priority;
	private String date;
	private int duration;
	private String description;

	public TaskEntry( final short priority, final int durationInMinutes, final String description ) {
		this.priority = priority;
		this.duration = durationInMinutes;
		this.description = description;
	}

	public long getId() {
		return this.id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public short getState() {
		return this.state;
	}

	public void setState( short state ) {
		this.state = state;
	}

	public short getPriority() {
		return this.priority;
	}

	public void setPriority( short priority ) {
		this.priority = priority;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate( String date ) {
		this.date = date;
	}

	public int getDuration() {
		return this.duration;
	}

	public void setDuration( int duration ) {
		this.duration = duration;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription( String description ) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( ( this.description == null ) ? 0 : this.description.hashCode() );
		result = prime * result + this.duration;
		result = prime * result + (int) ( this.id ^ ( this.id >>> 32 ) );
		result = prime * result + this.priority;
		result = prime * result + this.state;
		result = prime * result + ( ( this.date == null ) ? 0 : this.date.hashCode() );
		return result;
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj )
			return true;

		if ( obj == null || getClass() != obj.getClass() )
			return false;

		TaskEntry other = (TaskEntry) obj;

		if ( this.description == null ) {
			if ( other.description != null )
				return false;
		} else if ( !this.description.equals( other.description ) ) {
			return false;
		}

		if ( this.duration != other.duration ) {
			return false;
		}

		if ( this.id != other.id ) {
			return false;
		}

		if ( this.priority != other.priority ) {
			return false;
		}

		if ( this.state != other.state ) {
			return false;
		}

		if ( this.date != other.date ) {
			return false;
		}

		return true;
	}
}
