package com.randomcoder.article.moderation;

import com.randomcoder.article.comment.Comment;

/**
 * Interface for generic moderator plugins.
 * 
 * <pre>
 * Copyright (c) 2007, Craig Condit. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * </pre>
 */
public interface Moderator
{
	/**
	 * Validates the given comment as spam or ham.
	 * @param comment comment to validate
	 * @throws ModerationException if operation could not be performed
	 * @return true if comment is legitimate, false if comment is spam
	 */
	public boolean validate(Comment comment) throws ModerationException;
	
	/**
	 * Marks the given comment as spam.
	 * @param comment comment to mark as spam
	 * @throws ModerationException if operation could not be performed
	 */
	public void markAsSpam(Comment comment) throws ModerationException;
	
	/**
	 * Marks the given comment as ham (non-spam).
	 * @param comment comment to mark as ham
	 * @throws ModerationException if operation could not be performed
	 */
	public void markAsHam(Comment comment) throws ModerationException;
}
