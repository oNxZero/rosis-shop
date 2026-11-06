#!/usr/bin/env python3
"""Check local references, duplicate resources and Minecraft 1.21 data paths."""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
RESOURCE_ROOTS = [ROOT / "src" / source / "resources" for source in ["main", "generated"]]
ASSETS = [root / "assets/rosis_shop" for root in RESOURCE_ROOTS]


def exists(kind, identifier, suffix):
    return any((root / kind / (identifier + suffix)).is_file() for root in ASSETS)


def references(value, key=None):
    if isinstance(value, dict):
        for name, child in value.items():
            if name == "textures" and isinstance(child, dict):
                for texture in child.values():
                    if isinstance(texture, str) and texture.startswith("rosis_shop:"):
                        yield "textures", texture, ".png"
            else:
                yield from references(child, name)
    elif isinstance(value, list):
        for child in value:
            yield from references(child, key)
    elif isinstance(value, str) and key in {"parent", "model"} and value.startswith("rosis_shop:"):
        yield "models", value, ".json"


def main():
    count = 0
    errors = []
    seen = {}
    obsolete = {"advancements", "recipes", "loot_tables", "structures", "predicates",
                "item_modifiers", "functions"}
    obsolete_tags = {"blocks", "items", "entity_types", "fluids", "game_events", "functions"}
    for root in RESOURCE_ROOTS:
        for path in root.rglob("*"):
            if not path.is_file():
                continue
            relative = path.relative_to(root)
            if relative in seen:
                errors.append(f"Duplicate resource: {seen[relative].relative_to(ROOT)} and {path.relative_to(ROOT)}")
            seen[relative] = path
            parts = relative.parts
            if len(parts) >= 4 and parts[0] == "data":
                if parts[2] in obsolete or (parts[2] == "tags" and len(parts) >= 5 and parts[3] in obsolete_tags):
                    errors.append(f"Obsolete pre-1.21 data path: {path.relative_to(ROOT)}")
    for root in ASSETS:
        for path in root.rglob("*.json"):
            for kind, identifier, suffix in references(json.loads(path.read_text())):
                count += 1
                if not exists(kind, identifier.split(":", 1)[1], suffix):
                    errors.append(f"{path.relative_to(ROOT)}: missing {identifier}{suffix}")
    if not count:
        raise SystemExit("No resource references found; check asset paths")
    if errors:
        raise SystemExit("\n".join(errors))
    print(f"Passed: {count} local references resolve; no duplicate resources or obsolete data paths.")


if __name__ == "__main__":
    main()
