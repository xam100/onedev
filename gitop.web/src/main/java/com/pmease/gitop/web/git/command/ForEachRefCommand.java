package com.pmease.gitop.web.git.command;

import java.io.File;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.pmease.commons.git.command.GitCommand;
import com.pmease.commons.util.execution.Commandline;
import com.pmease.commons.util.execution.LineConsumer;

public abstract class ForEachRefCommand<T, V extends ForEachRefCommand<T, V>> extends GitCommand<T> {

	private String format;
	private String[] patterns = new String[0];
	private int count;
	private String sort;
	
	public ForEachRefCommand(File repoDir) {
		super(repoDir);
	}
	
	public ForEachRefCommand(File repoDir, Map<String, String> environments) {
		super(repoDir, environments);
	}

	public V format(String format) {
		this.format = format;
		return self();
	}
	
	public V patterns(String value, String... values) {
		if (value == null) {
			this.patterns = new String[0];
		} else {
			this.patterns = Iterables.toArray(ImmutableList.<String>builder()
								.add(value)
								.add(values)
								.build(), 
								String.class);
		}
		
		return self();
	}
	
	public V count(int count) {
		this.count = count;
		return self();
	}
	
	public V sort(String sort) {
		this.sort = sort;
		return self();
	}

	abstract protected V self();
	abstract protected ForEachRefOutputHandler<T> getOutputHandler();
	
	@Override
	public T call() {
		Commandline cmd = cmd();
		cmd.addArgs("for-each-ref");
		
		applyArgs(cmd);
		
		ForEachRefOutputHandler<T> outputHandler = getOutputHandler();
		cmd.execute(outputHandler, getErrorHandler());
		
		return outputHandler.getOutput();
	}

	protected LineConsumer getErrorHandler() {
		return new LineConsumer.ErrorLogger();
	}
	
	protected void applyArgs(Commandline cmd) {
		if (getCount() > 0) {
			cmd.addArgs("--count=" + String.valueOf(getCount()));
		}
		
		if (!Strings.isNullOrEmpty(getSort())) {
			cmd.addArgs("--sort=" + getSort());
		}
		
		if (!Strings.isNullOrEmpty(getFormat())) {
			cmd.addArgs("--format=" + getFormat());
		}
		
		for (String each : getPatterns()) {
			cmd.addArgs(each);
		}
	}

	public String getFormat() {
		return format;
	}

	public String[] getPatterns() {
		return patterns;
	}

	public int getCount() {
		return count;
	}

	public String getSort() {
		return sort;
	}
	
}
