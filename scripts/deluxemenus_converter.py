import os

import yaml
import re
import json

# Hello, this is a DeluxeMenus to SkiesGUIs converter script!
# This script will convert .yml files to .json files for use with the SkiesGUIs mod.
# This script is not perfect and may not convert everything correctly.

# Place your DeluxeMenus .yml files in the 'input' directory.
# Run this script to convert the .yml files to .json files.
# The converted .json files will be placed in the 'output' directory.

input_dir = 'input'
output_dir = 'output'

def convert_color(match_obj):
    if match_obj.group() is not None:
        match_result = match_obj.group().replace('&', '').replace('§', '')
        match match_result:
            case '0': return '<black>'
            case '1': return '<dark_blue>'
            case '2': return '<dark_green>'
            case '3': return '<dark_aqua>'
            case '4': return '<dark_red>'
            case '5': return '<dark_purple>'
            case '6': return '<gold>'
            case '7': return '<gray>'
            case '8': return '<dark_gray>'
            case '9': return '<blue>'
            case 'a': return '<green>'
            case 'b': return '<aqua>'
            case 'c': return '<red>'
            case 'd': return '<light_purple>'
            case 'e': return '<yellow>'
            case 'f': return '<white>'
            case 'k': return '<obf>'
            case 'l': return '<b>'
            case 'm': return '<st>'
            case 'n': return '<u>'
            case 'o': return '<i>'
            case 'r': return '<reset>'
            case _:
                if match_result.startswith('#'):
                    return f"<{match_result}>"
                else:
                    print("Unsupported Color Found: " + match_result)

def parse_color(input):
    return (re.sub(r'[&§][0-9a-fklmnor]|[&§]#([0-9a-fA-F]{6})', convert_color, input)
            .replace("%player_name%", "%player%"))

def parse_requirements(requirements):
    requirements_data = {}

    # 'requirements'
    requirements_map = {}
    for id in requirements['requirements'].keys():
        requirement = requirements['requirements'][id]
        type = requirement['type']
        match type:
            case 'has permission':
                requirements_map[id] = {
                    "type": "PERMISSION",
                    "permission": requirement['permission']
                }
            case 'has money' | '!has money':
                is_not = type.startswith('!')
                requirements_map[id] = {
                    "type": "CURRENCY",
                    "comparison": "<=" if is_not else ">=",
                    "amount": requirement['amount'],
                    "currency": ""
                }
            case 'has item' | '!has item':
                is_not = type.startswith('!')
                requirement_map = {
                    "type": "ITEM",
                    "comparison": "<=" if is_not else ">="
                }
                if 'material' in requirement:
                    requirement_map['item'] = requirement['material']
                if 'amount' in requirement:
                    requirement_map['amount'] = requirement['amount']
                if 'modeldata' in requirement:
                    requirement_map['custom_model_data'] = requirement['modeldata']
                if 'strict' in requirement:
                    requirement_map['strict'] = requirement['strict']
                if 'nbt' in requirement:
                    requirement_map['nbt'] = requirement['nbt']

                requirements_map[id] = requirement_map
            case 'javascript':
                requirements_map[id] = {
                    "type": "JAVASCRIPT",
                    "expression": requirement['expression']
                }
            case 'string equals' | '!string equals' | 'string equals ignorecase' | '!string equals ignorecase':
                is_not = type.startswith('!')
                is_strict = 'ignorecase' not in type
                requirements_map[id] = {
                    "type": "PLACEHOLDER",
                    "comparison": "!=" if is_not else "==",
                    "input": requirement['input'],
                    "output": requirement['output'],
                    "strict": is_strict
                }
            case '==' | '!=' | '>=' | '<=' | '>' | '<':
                requirements_map[id] = {
                    "type": "PLACEHOLDER",
                    "comparison": type,
                    "input": requirement['input'],
                    "output": requirement['output']
                }
            case _:
                print(f"Unsupported Requirement Found: {type}")
        # 'success_commands'
        if 'success_commands' in requirements:
            success_actions_map = {}
            for i, action in enumerate(requirement['success_commands']):
                success_actions_map[i] = parse_actions(action, "ANY")
            requirements_map[id]['success_actions'] = success_actions_map

        # 'deny_commands'
        if 'deny_commands' in requirements:
            deny_actions_map = {}
            for i, action in enumerate(requirement['deny_commands']):
                deny_actions_map[i] = parse_actions(action, "ANY")
            requirements_map[id]['deny_actions'] = deny_actions_map

    requirements_data['requirements'] = requirements_map

    # 'success_commands'
    if 'success_commands' in requirements:
        success_actions_map = {}
        for i, action in enumerate(requirements['success_commands']):
            success_actions_map[i] = parse_actions(action, "ANY")
        requirements_data['success_actions'] = success_actions_map

    # 'deny_commands'
    if 'deny_commands' in requirements:
        deny_actions_map = {}
        for i, action in enumerate(requirements['deny_commands']):
            deny_actions_map[i] = parse_actions(action, "ANY")
        requirements_data['deny_actions'] = deny_actions_map

    return requirements_data

def parse_actions(actions, click):
    actions_map = {}
    for i, action in enumerate(actions):
        match_obj = re.search(r'\[(.*?)\]\s*(.*)', action)
        if match_obj.group() is not None:
            match match_obj.group(1):
                case 'player' | 'commandevent':
                    actions_map[i] = {
                        "type": "COMMAND_PLAYER",
                        "click": click,
                        "commands": [parse_color(match_obj.group(2))]
                    }
                case 'console':
                    actions_map[i] = {
                        "type": "COMMAND_CONSOLE",
                        "click": click,
                        "commands": [parse_color(match_obj.group(2))]
                    }
                case 'message' | 'minimessage':
                    actions_map[i] = {
                        "type": "MESSAGE",
                        "click": click,
                        "message": [parse_color(match_obj.group(2))]
                    }
                case 'broadcast' | 'minibroadcast':
                    actions_map[i] = {
                        "type": "BROADCAST",
                        "click": click,
                        "message": [parse_color(match_obj.group(2))]
                    }
                case 'openguimenu':
                    actions_map[i] = {
                        "type": "OPEN_GUI",
                        "click": click,
                        "id": match_obj.group(2)
                    }
                case 'close':
                    actions_map[i] = {
                        "type": "CLOSE_GUI",
                        "click": click
                    }
                case 'refresh':
                    actions_map[i] = {
                        "type": "REFRESH_GUI",
                        "click": click
                    }
                case 'sound':
                    sound_split = match_obj.group(2).split(' ')
                    volume = sound_split[1] if len(sound_split) > 1 else 1.0
                    pitch = sound_split[2] if len(sound_split) > 2 else 1.0
                    actions_map[i] = {
                        "type": "PLAYSOUND",
                        "click": click,
                        "sound": sound_split[0],
                        "volume": volume,
                        "pitch": pitch
                    }
                case 'takemoney':
                    actions_map[i] = {
                        "type": "CURRENCY_WITHDRAW",
                        "click": click,
                        "amount": match_obj.group(2),
                        "currency": ""
                    }
                case 'givemoney':
                    actions_map[i] = {
                        "type": "CURRENCY_DEPOSIT",
                        "click": click,
                        "amount": match_obj.group(2),
                        "currency": ""
                    }
                case 'takeexp' | 'giveexp':
                    level_mode = "L" in match_obj.group(2)
                    amount = match_obj.group(2).replace("L", "")
                    actions_map[i] = {
                        "type": "GIVE_EXP",
                        "click": click,
                        "amount": int(amount),
                        "level": level_mode
                    }
                case _:
                    print(f"Unsupported Action Found: [{match_obj.group(1)}] {match_obj.group(2)}")
    return actions_map

def parse_items(items):
    items_map = {}

    for id in items.keys():
        item = items[id]
        item_map = {
            "item": item['material']
        }
        if 'priority' in item:
            item_map['priority'] = item['priority']
        if 'slot' in item:
            item_map['slots'] = [item['slot']]
        if 'slots' in item:
            slots = []
            dm_slots = item['slots']
            for slot in dm_slots:
                if not isinstance(slot, int) and '-' in slot:
                    slot_range = slot.split('-')
                    slots += list(range(int(slot_range[0]), int(slot_range[1]) + 1))
                else:
                    slots.append(int(slot))
            item_map['slots'] = slots
        if 'display_name' in item:
            item_map['name'] = parse_color(item['display_name'])
        if 'lore' in item:
            item_map['lore'] = [parse_color(lore) for lore in item['lore']]
        if 'view_requirement' in item:
            item_map['view_requirements'] = parse_requirements(item['view_requirement'])

        item_nbt = {}
        if 'nbt_string' in item:
            nbt = item['nbt_string'].split(':', 1)
            item_nbt[nbt[0]] = nbt[1]
        if 'nbt_strings' in item:
            for nbt in item['nbt_strings']:
                nbt_split = nbt.split(':', 1)
                result = re.search(r'^\[.*\]$', nbt_split[1])
                if result is not None:
                    if '[]' in result.group(0):
                        item_nbt[nbt_split[0]] = []
                    else:
                        split = result.group(0).replace("[", "").replace("]", "").split(',')
                        item_nbt[nbt_split[0]] = split
                else:
                    item_nbt[nbt_split[0]] = nbt_split[1]
        if 'nbt_int' in item:
            nbt = item['nbt_int'].split(':', 1)
            item_nbt[nbt[0]] = int(nbt[1])
        if 'nbt_ints' in item:
            for nbt in item['nbt_ints']:
                nbt_split = nbt.split(':', 1)
                item_nbt[nbt_split[0]] = int(nbt_split[1])

        if item_nbt is not {}:
            item_map['nbt'] = item_nbt

        action_types = {
            "click_commands": "ANY",
            "left_click_commands": "LEFT_CLICK",
            "right_click_commands": "RIGHT_CLICK",
            "middle_click_commands": "MIDDLE_CLICK",
            "shift_click_commands": "ANY_SHIFT_CLICK",
            "shift_left_click_commands": "SHIFT_LEFT_CLICK",
            "shift_right_click_commands": "SHIFT_RIGHT_CLICK",
        }
        item_actions = {}
        for key, value in action_types.items():
            if key in item:
                actions = parse_actions(item[key], value)
                item_actions.update(actions)

        if item_actions is not {}:
            item_map['actions'] = item_actions


        items_map[id] = item_map

    return items_map


print("Iterating through the .yml files in the 'input' directory and converting them...")
os.makedirs(input_dir, exist_ok=True)
for root, dirs, files in os.walk(input_dir):
    for filename in files:
        if filename.lower().endswith('.yml'):
            input_path = os.path.join(root, filename)
            output_root = root.replace(input_dir, output_dir)
            output_path = os.path.join(output_root, filename.replace(".yml", ".json"))
            os.makedirs(os.path.dirname(output_path), exist_ok=True)
            with open(input_path, 'r', encoding='utf-8') as f:
                data = yaml.load(f, Loader=yaml.FullLoader)

                title = parse_color(data['menu_title']) if 'menu_title' in data else None
                open_command = data['open_command'] if 'open_command' in data else None
                open_requirements = parse_requirements(data['open_requirements']) if 'open_requirements' in data else None
                open_commands = parse_actions(data['open_commands']) if 'open_commands' in data else None
                size = data['size'] if 'size' in data else None
                items = parse_items(data['items']) if 'items' in data else None

                with open(output_path, 'w', encoding='utf-8') as o:
                    node = {}

                    if title is not None:
                        node['title'] = title
                    if size is not None:
                        node['size'] = size
                    if open_command is not None:
                        node['alias_commands'] = [open_command]
                    if open_requirements is not None:
                        node['open_requirements'] = open_requirements
                    if open_commands is not None:
                        node['open_actions'] = open_commands
                    if items is not None:
                        node['items'] = items

                    json.dump(node, o, indent=4, ensure_ascii=False)
