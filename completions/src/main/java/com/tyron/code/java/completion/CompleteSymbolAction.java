package com.tyron.code.java.completion;

import com.google.common.collect.ImmutableList;
import com.tyron.code.java.analysis.AnalysisResult;
import com.tyron.code.project.model.ProjectModule;
import com.tyron.code.project.util.ModuleUtils;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import shadow.com.sun.source.util.TreePath;
import shadow.com.sun.source.util.Trees;
import shadow.com.sun.tools.javac.code.Symtab;
import shadow.com.sun.tools.javac.util.Context;
import shadow.javax.lang.model.element.Element;
import shadow.javax.lang.model.element.ModuleElement;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.StreamSupport;

public class CompleteSymbolAction implements CompletionAction {


    @Override
    public ImmutableList<CompletionCandidate> getCompletionCandidates(CompletionArgs args) {
        ImmutableList.Builder<CompletionCandidate> builder = ImmutableList.builder();
        ProjectModule module = args.module();

        List<ClassForImportCandidate> list = ModuleUtils.getAllClasses(module).stream()
                .parallel()
                .filter(it -> FuzzySearch.partialRatio(args.prefix(), it.fileName()) >= 85)
                .map(it -> new ClassForImportCandidate(String.join(".", it.qualifiers()), it.fileName(), it.path().toString()))
                .toList();
        builder.addAll(list);


        builder.addAll(completeUsingScope(args.currentAnalyzedPath(), args.analysisResult(), args.prefix()));
        return builder.build();
    }

    private List<CompletionCandidate> completeUsingScope(TreePath treePath, AnalysisResult analysisResult, String prefix) {
        analysisResult.analyzer().checkCancelled();
        List<CompletionCandidate> list = new ArrayList<>();
        var trees = Trees.instance(analysisResult.javacTask());
        var scope = trees.getScope(treePath);
        List<Element> elements = ScopeHelper.scopeMembers(analysisResult.javacTask(), scope, it -> it.toString().contains(prefix));
        for (Element element : elements) {
            list.add(new ElementCompletionCandidate(element));
        }
        return list;
    }

}
