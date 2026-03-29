package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.data.policy.PolicyItem;
import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import de.marcschuler.webrtcserver.dto.data.PolicyItemDTO;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import de.marcschuler.webrtcserver.dto.data.policy.RolePolicyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION)

public abstract class PolicyMapper {

    public abstract PolicyItemDTO mapToDTO(PolicyItem policyItem);

    @SubclassMapping(source = RolePolicy.class, target = RolePolicyDTO.class)
    public abstract PolicyDTO mapToDTO(Policy policy);
    public abstract RolePolicyDTO mapToDTO(RolePolicy rolePolicy);

    @SubclassMapping(source = RolePolicyDTO.class, target = RolePolicy.class)
    public abstract Policy mapFromDTO(PolicyDTO policyDTO);
    public abstract RolePolicy mapFromDTO(RolePolicyDTO rolePolicyDTO);


    public abstract RolePolicy update(@MappingTarget RolePolicy policy, RolePolicyDTO dto);

    public Policy update(@MappingTarget Policy policy, PolicyDTO dto) {
        return switch (policy) {
            case RolePolicy rolePolicy when dto instanceof RolePolicyDTO -> update(rolePolicy, (RolePolicyDTO) dto);
            case null, default -> throw new IllegalArgumentException("Unsupported policy type: " + dto.getClass());
        };
    }
}
