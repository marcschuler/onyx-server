package de.marcschuler.webrtcserver.mapper;

import de.marcschuler.webrtcserver.data.policy.Policy;
import de.marcschuler.webrtcserver.data.policy.RolePolicy;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyWriteDTO;
import de.marcschuler.webrtcserver.dto.data.policy.PolicyDTO;
import de.marcschuler.webrtcserver.dto.data.policy.RolePolicyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring", subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION, uses = ServerMapper.class)

public abstract class PolicyMapper {

    public abstract PolicyDTO mapToDTO(Policy policy);

    @SubclassMapping(source = RolePolicy.class, target = RolePolicyDTO.class)
    public abstract RolePolicyDTO mapToDTO(RolePolicy entity);

    @SubclassMapping(source = RolePolicyDTO.class, target = RolePolicy.class)
    public abstract Policy mapFromDTO(PolicyWriteDTO policyWriteDTO);

    public abstract Policy update(@MappingTarget Policy policy, PolicyWriteDTO policyWriteDTO);

}
