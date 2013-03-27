package com.ahaines.ai.search.service.heurstic.service;

import java.util.Collection;
import java.util.Comparator;

import com.ahaines.ai.search.model.Identifiable;
import com.ahaines.ai.search.service.NodeVisitor;
import com.ahaines.ai.search.service.SearchService;
import com.ahaines.ai.search.service.SuccessorNodeService;

public class HeuristicSearchService<T extends Identifiable> extends SearchService<T>{

	private HeuristicSearchService(SuccessorNodeService<T> successorService, Iterable<NodeVisitor<T>> visitors) {
		super(successorService, visitors);
	}

	public static class HeuristicSearchServiceBuilder<T extends Identifiable> extends SearchServiceBuilder<T>{

		public HeuristicSearchServiceBuilder(SuccessorNodeService<T> successorService) {
			super(successorService);
		}
		
		@Override
		public HeuristicSearchServiceBuilder<T> registerVisitor(NodeVisitor<? super T> visitor){
			super.registerVisitor(visitor);
			return this;
		}
		
		@Override
		public HeuristicSearchService<T> build(){
			return new HeuristicSearchService<T>(successorService, (Collection)visitors);
		}
		
	}
}
