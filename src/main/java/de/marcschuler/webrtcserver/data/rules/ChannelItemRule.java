package de.marcschuler.webrtcserver.data.rules;

import lombok.Data;

import java.util.Set;

@Data
public class ChannelItemRule {

    private Set<RuleSet> sets;
    private Set<Rule> rules;
}
