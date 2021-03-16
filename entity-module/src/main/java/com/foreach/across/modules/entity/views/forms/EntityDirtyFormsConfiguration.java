/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.views.forms;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Configuration class that supports customizing the <a href="https://github.com/snikch/jquery.dirtyforms">jquery dirtyforms</a> plugin.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityDirtyFormsConfiguration
{
	private String message;
	private String dirtyClass;
	private String listeningClass;
	private String ignoreClass;
	private String ignoreSelector;
	private String fieldSelector;
	private Object dialog;
	private boolean debug;

	public EntityDirtyFormsConfiguration message( String message ) {
		this.message = message;
		return this;
	}

	public EntityDirtyFormsConfiguration dirtyClass( String dirtyClass ) {
		this.dirtyClass = dirtyClass;
		return this;
	}

	public EntityDirtyFormsConfiguration listeningClass( String listeningClass ) {
		this.listeningClass = listeningClass;
		return this;
	}

	public EntityDirtyFormsConfiguration ignoreClass( String ignoreClass ) {
		this.ignoreClass = ignoreClass;
		return this;
	}

	public EntityDirtyFormsConfiguration ignoreSelector( String ignoreSelector ) {
		this.ignoreSelector = ignoreSelector;
		return this;
	}

	public EntityDirtyFormsConfiguration fieldSelector( String fieldSelector ) {
		this.fieldSelector = fieldSelector;
		return this;
	}

	public EntityDirtyFormsConfiguration debug( boolean debug ) {
		this.debug = debug;
		return this;
	}

	public EntityDirtyFormsConfiguration dialog( boolean useDialog ) {
		if ( !useDialog ) {
			this.dialog = false;
		}
		return this;
	}

	// todo should configure a bootstrap modal on the page
	public EntityDirtyFormsConfiguration dialog( DialogSettings dialogSettings ) {
		this.dialog = dialogSettings;
		return this;
	}

	public class DialogSettings
	{
		private String title;
		private String proceedButtonClass;
		private String proceedButtonText;
		private String stayButtonClass;
		private String stayButtonText;
		private String dialogID;
		private String titleID;
		private String messageClass;
		private String preMessageText;
		private String postMessageText;
		private String replaceText;

		public DialogSettings title( String title ) {
			this.title = title;
			return this;
		}

		public DialogSettings proceedButtonClass( String proceedButtonClass ) {
			this.proceedButtonClass = proceedButtonClass;
			return this;
		}

		public DialogSettings proceedButtonText( String proceedButtonText ) {
			this.proceedButtonText = proceedButtonText;
			return this;
		}

		public DialogSettings stayButtonClass( String stayButtonClass ) {
			this.stayButtonClass = stayButtonClass;
			return this;
		}

		public DialogSettings stayButtonText( String stayButtonText ) {
			this.stayButtonText = stayButtonText;
			return this;
		}

		public DialogSettings dialogID( String dialogID ) {
			this.dialogID = dialogID;
			return this;
		}

		public DialogSettings titleID( String titleID ) {
			this.titleID = titleID;
			return this;
		}

		public DialogSettings messageClass( String messageClass ) {
			this.messageClass = messageClass;
			return this;
		}

		public DialogSettings preMessageText( String preMessageText ) {
			this.preMessageText = preMessageText;
			return this;
		}

		public DialogSettings postMessageText( String postMessageText ) {
			this.postMessageText = postMessageText;
			return this;
		}

		public DialogSettings replaceText( String replaceText ) {
			this.replaceText = replaceText;
			return this;
		}
	}

}
