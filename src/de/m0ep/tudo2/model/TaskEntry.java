package de.m0ep.tudo2.model;

import java.io.Serializable;
import java.util.Calendar;

import de.m0ep.tudo2.ObjectUtils;

public class TaskEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String STATUS_NOT_COMPLETED = "not_completed";
	public static final String STATUS_COMPLETED = "completed";
	public static final String STATUS_MOVED = "moved";

	private long id;

	private Calendar completed;
	private Calendar due;
	private Calendar updated;

	private String note;
	private String priority;
	private String status;
	private String title;

	private boolean deleted;

	public long getId() {
		return this.id;
	}

	public void setId( long id ) {
		this.id = id;
	}

	public Calendar getCompleted() {
		return this.completed;
	}

	public void setCompleted( Calendar completed ) {
		this.completed = completed;
	}

	public Calendar getDue() {
		return this.due;
	}

	public void setDue( Calendar due ) {
		this.due = due;
	}

	public Calendar getUpdated() {
		return this.updated;
	}

	public void setUpdated( Calendar updated ) {
		this.updated = updated;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote( String note ) {
		this.note = note;
	}

	public String getPriority() {
		return this.priority;
	}

	public void setPriority( String priority ) {
		this.priority = priority;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus( String status ) {
		this.status = status;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle( String title ) {
		this.title = title;
	}

	public boolean isDeleted() {
		return this.deleted;
	}

	public void setDeleted( boolean deleted ) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(
		        completed,
		        deleted,
		        due,
		        id,
		        note,
		        priority,
		        status,
		        title,
		        updated );
	}

	@Override
	public boolean equals( Object obj ) {
		if ( this == obj ) {
			return true;
		}

		if ( null == obj || getClass() != obj.getClass() ) {
			return false;
		}

		TaskEntry other = (TaskEntry) obj;

		return ObjectUtils.equals( this.completed, other.completed )
		        && ObjectUtils.equals( this.deleted, other.deleted )
		        && ObjectUtils.equals( this.due, other.due )
		        && ObjectUtils.equals( this.id, other.id )
		        && ObjectUtils.equals( this.note, other.note )
		        && ObjectUtils.equals( this.priority, other.priority )
		        && ObjectUtils.equals( this.status, other.status )
		        && ObjectUtils.equals( this.title, other.title )
		        && ObjectUtils.equals( this.updated, other.updated );
	}

	@Override
	public String toString() {
		return "TaskEntry ["
		        + "id=" + this.id
		        + ", completed=" + this.completed
		        + ", due=" + this.due
		        + ", updated=" + this.updated
		        + ", note=" + this.note
		        + ", priority=" + this.priority
		        + ", status=" + this.status
		        + ", title=" + this.title
		        + ", deleted=" + this.deleted + "]";
	}

}
