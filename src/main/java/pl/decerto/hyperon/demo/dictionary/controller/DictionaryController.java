package pl.decerto.hyperon.demo.dictionary.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static pl.decerto.hyperon.demo.dictionary.controller.ControllerResponseFactory.response;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import pl.decerto.hyperon.demo.dictionary.dto.ControllerResponseDto;
import pl.decerto.hyperon.demo.dictionary.dto.MultiDictionaryDto;
import pl.decerto.hyperon.demo.dictionary.dto.SingleDictionaryDto;
import pl.decerto.hyperon.demo.dictionary.service.DictionaryService;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryController {

	static final String ONLY_FOR_KEY_DICT_NOTE = "Works only for dictionaries accessible by key. " +
		"Produces error result for dictionaries accessible only by context.";
	static final String DICT_CODE_DESC = "code of dictionary";
	static final String LEVEL_CODE_DESC = "code of selected level";

	private final DictionaryService dictionaryService;

	@Autowired
	public DictionaryController(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@ApiOperation("Get list of all available dictionary codes")
	public ResponseEntity<ControllerResponseDto<List<String>>> getDictionaryCodes() {

		return response(dictionaryService.getAllDictionariesCodes());
	}

	@GetMapping(value = "/by-code", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get data from selected dictionary", notes = ONLY_FOR_KEY_DICT_NOTE)
	public ResponseEntity<ControllerResponseDto<MultiDictionaryDto>> getDictionary(
		@RequestParam @ApiParam(DICT_CODE_DESC) String dictionary) {

		var dict = dictionaryService.getDictionaryByCode(dictionary);
		return response(new MultiDictionaryDto(dict));
	}

	@GetMapping(value = "/by-code/by-level", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get single-value dictionary constructed from multi-value dictionary by " +
		"restricting output to specified output level",
		notes = ONLY_FOR_KEY_DICT_NOTE)
	public ResponseEntity<ControllerResponseDto<SingleDictionaryDto>> getSubDictionaryForLevel(
		@RequestParam @ApiParam(DICT_CODE_DESC) String dictionary,
		@ApiParam(LEVEL_CODE_DESC) @RequestParam String level) {

		var dict = dictionaryService.getDictionaryLevel(dictionary, level);
		return response(new SingleDictionaryDto(dict));
	}

	@GetMapping(value = "/by-code/levels", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get list of all output levels for specified dictionary",
		notes = ONLY_FOR_KEY_DICT_NOTE)
	public ResponseEntity<ControllerResponseDto<Set<String>>> getDictionaryLevels(
		@RequestParam @ApiParam(DICT_CODE_DESC) String dictionary) {

		return response(dictionaryService.getDictionaryLevels(dictionary));
	}

}
