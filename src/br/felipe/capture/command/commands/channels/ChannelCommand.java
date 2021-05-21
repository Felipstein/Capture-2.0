package br.felipe.capture.command.commands.channels;

import br.felipe.capture.command.Command;

public abstract class ChannelCommand extends Command {
	
	public ChannelCommand(String syntax) {
		super(syntax);
	}
	
	public ChannelCommand(String syntax, boolean requiresOp) {
		super(syntax, requiresOp);
	}
	
	protected String buildMessage(String[] args, int indexStart) {
		StringBuilder string = new StringBuilder();
		for(int i = indexStart - 1; i < args.length; ++i) {
			string.append((i == (indexStart - 1) ? "" : " ") + args[i]);
		}
		return string.toString();
	}
	
}