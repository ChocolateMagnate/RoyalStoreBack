package com.royal.products.service;

import com.royal.errors.HttpException;
import com.royal.products.domain.ElectronicProduct;
import com.royal.products.domain.requests.RawElectronicProductRequest;
import com.royal.products.repository.ElectronicProductRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class ElectronicProductService {
    private final ElectronicProductRepository electronicProductRepository;
    private final MongoTemplate template;

    public ElectronicProductService(@Autowired ElectronicProductRepository repository,
                                    @Autowired MongoTemplate template) {
        this.electronicProductRepository = repository;
        this.template = template;
    }

    public boolean productExistsById(String id) {
        return this.electronicProductRepository.existsById(id);
    }

    public List<ElectronicProduct> getRandomStock() {
        List<ElectronicProduct> products = this.electronicProductRepository.findAll()
                .stream().limit(300).toList();
        List<ElectronicProduct> results = Stream.of(products).flatMap(Collection::stream)
                .collect(Collectors.toList());
        Collections.shuffle(results);
        return results;
    }

    public List<ElectronicProduct> getProductsByDescription(@NotNull String description) {
        // We replace all spaces in the user query because the method must return all products that
        // contain at least one of the words typed in the query, so we generate a regular expression
        // that will look for these keywords. We turn the string into lower case to avoid case mismatches
        // and replace the delimiter (space) with the pipe (|) to make the expression that will match any
        // description that contains one of these tokens. Finally, we need to surround them in parentheses
        // to declare that any of the tokens match individually, and dots to denote they can be surrounded
        // with any other tokens.
        if (description.contains(" ")) {
            String keywords = description.toLowerCase().replace(" ", "|");
            String keywordSeededRegularExpression = ".*(" + keywords + ").*";
            Pattern keywordRecurringPattern = Pattern.compile(keywordSeededRegularExpression);
            return this.electronicProductRepository.findAll().stream().filter(
                            product -> keywordRecurringPattern.matcher(product.getDescription()).matches())
                    .toList();
        } else {
            String casefoldDescription = description.toLowerCase();
            return this.electronicProductRepository.findAll().stream().filter(
                    product -> product.getDescription().toLowerCase().contains(casefoldDescription))
                    .toList();
        }
    }

    public List<ElectronicProduct> getProductsByParameters(Query query) {
        List<ElectronicProduct> products = this.template.find(query, ElectronicProduct.class);
        return Stream.of(products).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Optional<ElectronicProduct> retrieveProductById(String id) {
        return this.electronicProductRepository.findById(id);
    }

    public ArrayList<ElectronicProduct> retrieveProductsFromIds(@NotNull ArrayList<String> ids) {
        ArrayList<ElectronicProduct> products = new ArrayList<>(ids.size());
        for (String id : ids) this.electronicProductRepository.findById(id).ifPresent(products::add);
        return products;
    }

    public String createNewProduct(@NotNull RawElectronicProductRequest initializer) throws HttpException {
        try {
            if (initializer.containsNullFields()) {
                String nullFields = String.join(", ", initializer.getNullFields());
                String trimmedNullFields = nullFields.substring(0, nullFields.length() - 2);
                throw new HttpException(HttpStatus.BAD_REQUEST, "No fields are allowed to be empty: " + trimmedNullFields);
            }

            ElectronicProduct product = ElectronicProduct.build(initializer);
            this.electronicProductRepository.save(product);
            return product.getId();
        } catch (IOException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "The 'photo' parameter in request my be a valid picture.");
        }
    }

    public void updateExistingProduct(@NotNull RawElectronicProductRequest replacement) throws HttpException {
        try {
            ElectronicProduct oldProduct = handleEdgeCasesForUpdatingAndFindOldProduct(replacement);
            ElectronicProduct newProduct = ElectronicProduct.build(replacement);
            ElectronicProduct result = ElectronicProduct.yieldValidProductUsing(newProduct, oldProduct);
            this.electronicProductRepository.save(result);
        } catch (IOException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    public void updateExistingProduct(ElectronicProduct replacement) throws HttpException {
        ElectronicProduct oldProduct = handleEdgeCasesForUpdatingAndFindOldProduct(replacement);
        ElectronicProduct result = ElectronicProduct.yieldValidProductUsing(replacement, oldProduct);
        this.electronicProductRepository.save(result);
    }

    private @NotNull ElectronicProduct handleEdgeCasesForUpdatingAndFindOldProduct(
            @NotNull ElectronicProduct replacement) throws HttpException {
        if (replacement.getId() == null) throw new HttpException(HttpStatus.BAD_REQUEST, "No id supplied.");
        Optional<ElectronicProduct> optionalElectronicProduct = this.electronicProductRepository.findById(replacement.getId());
        if (optionalElectronicProduct.isEmpty()) throw new HttpException(
                HttpStatus.NOT_FOUND, "No product under id " + replacement.getId() + " exists.");
        else if (optionalElectronicProduct.get().getCategory() != replacement.getCategory())
            throw new HttpException(HttpStatus.BAD_REQUEST, "Cannot update a product between categories. " +
                    "Prefer to delete the old product and creating a new one.");
        return optionalElectronicProduct.get();
    }

    private @NotNull ElectronicProduct handleEdgeCasesForUpdatingAndFindOldProduct(
            @NotNull RawElectronicProductRequest replacement) throws HttpException {
        if (replacement.containsNullFields()) throw new HttpException(HttpStatus.BAD_REQUEST,
                "No request fields can be null: " + replacement.getNullFields());
        try {
            return handleEdgeCasesForUpdatingAndFindOldProduct(ElectronicProduct.build(replacement));
        } catch (IOException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    public void deleteProductById(String id) throws HttpException {
        if (!this.electronicProductRepository.existsById(id))
            throw new HttpException(HttpStatus.NOT_FOUND, "No product under the id " + id);
        this.electronicProductRepository.deleteById(id);
    }
}
